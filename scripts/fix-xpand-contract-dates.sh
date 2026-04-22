#!/usr/bin/env bash
#
# fix-xpand-contract-dates.sh
#
# One-shot migration: PATCH Contract-service records whose endDate,
# currentPeriod.startDate or currentPeriod.endDate were imported incorrectly
# from Xpand. Input is a CSV produced from the IssueList sheet of
# contract_date_mismatch_review.xlsx (only include=true rows, only those three
# fields). One PATCH call per contract is issued, carrying exactly the fields
# that need updating.
#
# ----------------------------------------------------------------------------
# RUN INSTRUCTIONS
# ----------------------------------------------------------------------------
# 1. Fill in CLIENT_ID, CLIENT_SECRET and verify WSO2_BASE_URL / TOKEN_URL /
#    API_PATH_PREFIX in the config block below.
#
# 2. Always dry-run first to inspect the generated PATCH bodies:
#
#      ./scripts/fix-xpand-contract-dates.sh
#
#    Look at scripts/logs/run-<ts>/payloads.ndjson before proceeding.
#
# 3. Smoke-test against the TEST environment. Test has different contractIds
#    than prod, so look them up by arrendekontrakt (-e) and execute (-x):
#
#      ./scripts/fix-xpand-contract-dates.sh -e -x
#
# 4. Run against PROD. The CSV already carries prod contractIds, so no
#    lookup is needed:
#
#      ./scripts/fix-xpand-contract-dates.sh -x
#
# Other flags: -f <csv>   alternative CSV file
#              -m <id>    override municipalityId (default 2281)
#              -t <ms>    throttle between calls (default 100)
#              -v         verbose (log every request/response body)
#              -h         help
#
# Defaults to dry-run — passing -x is required to issue real PATCH calls.
# ----------------------------------------------------------------------------

set -euo pipefail

# ============================================================================
# EDIT BEFORE RUNNING
# ============================================================================
WSO2_BASE_URL="https://wso2-gateway-host.se"		# WSO2 gateway base URL
TOKEN_URL="${WSO2_BASE_URL}/token"					# OAuth2 token endpoint
CLIENT_ID=""										# WSO2 app client id
CLIENT_SECRET=""									# WSO2 app client secret
API_PATH_PREFIX="/contract/7.1"						# gateway route to contract service
MUNICIPALITY_ID="2281"								# Sundsvall
USE_EXTERNAL_REF="false"							# true = look up contractId by arrendekontrakt
DRY_RUN="true"										# true by default; pass -x to mutate
THROTTLE_MS="100"									# sleep between PATCH calls
VERBOSE="false"
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_CSV="${SCRIPT_DIR}/data/prod_contract_date_fixes.csv"
CSV_FILE="${DEFAULT_CSV}"

RUN_TS="$(date +%Y%m%dT%H%M%S)"
LOG_ROOT="${SCRIPT_DIR}/logs/run-${RUN_TS}"

# Runtime state (set during execution)
ACCESS_TOKEN=""
TOKEN_EXPIRES_AT=0
declare -A RESOLVED_CONTRACT_ID   # externalRef -> contractId cache
OK_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0
TOTAL=0

usage() {
    cat <<EOF
Usage: ${0##*/} [options]

  -f <csv>   CSV file (default: scripts/data/prod_contract_date_fixes.csv)
  -e         Look up contractId by externalReferenceId (arrendekontrakt)
             instead of using CSV column B. Use when running against an
             environment whose contractIds differ from prod.
  -x         Execute PATCH calls (default is dry-run).
  -m <id>    Override municipality id (default: ${MUNICIPALITY_ID}).
  -t <ms>    Throttle between PATCH calls in milliseconds
             (default: ${THROTTLE_MS}).
  -v         Verbose logging (prints full request/response bodies).
  -h         Show this help and exit.

Expected CSV columns (header row required):
  arrendekontrakt,contractId,field,expected_value,issue_type

Credentials and URLs are configured at the top of this script.
EOF
}

# log() writes to stderr and the run log — NEVER stdout. Stdout is reserved
# for function return values captured via $(...) (resolve_contract_id,
# patch_contract, build_body, json_string, json_number). Mixing log output
# onto stdout would contaminate those return values.
log() {
    local line
    line="$(date +%H:%M:%S) $*"
    printf '%s\n' "${line}" >> "${LOG_ROOT}/run.log"
    printf '%s\n' "${line}" >&2
}
logv()  { [[ "${VERBOSE}" == "true" ]] && log "$@" || true; }
fatal() { log "FATAL: $*"; exit 1; }

parse_args() {
    while getopts ":f:em:t:xvh" opt; do
        case "$opt" in
            f) CSV_FILE="$OPTARG" ;;
            e) USE_EXTERNAL_REF="true" ;;
            m) MUNICIPALITY_ID="$OPTARG" ;;
            t) THROTTLE_MS="$OPTARG" ;;
            x) DRY_RUN="false" ;;
            v) VERBOSE="true" ;;
            h) usage; exit 0 ;;
            \?) echo "Unknown option: -$OPTARG" >&2; usage >&2; exit 2 ;;
            :)  echo "Option -$OPTARG requires an argument" >&2; exit 2 ;;
        esac
    done
}

preflight() {
    mkdir -p "${LOG_ROOT}"

    for bin in curl awk sort sed tr grep; do
        command -v "$bin" >/dev/null 2>&1 || fatal "Required binary not found: $bin"
    done

    [[ -f "${CSV_FILE}" ]] || fatal "CSV not found: ${CSV_FILE}"

    # Strip BOM + CRLF in place into a normalized working copy
    NORM_CSV="${LOG_ROOT}/input.normalized.csv"
    sed '1s/^\xef\xbb\xbf//' "${CSV_FILE}" | tr -d '\r' > "${NORM_CSV}"

    local header
    header="$(head -n 1 "${NORM_CSV}")"
    local expected="arrendekontrakt,contractId,field,expected_value,issue_type"
    [[ "${header}" == "${expected}" ]] \
        || fatal "Unexpected CSV header: '${header}' (expected: '${expected}')"

    if [[ "${DRY_RUN}" != "true" ]]; then
        [[ -n "${CLIENT_ID}"     ]] || fatal "CLIENT_ID is empty; edit the top of this script before running with -x"
        [[ -n "${CLIENT_SECRET}" ]] || fatal "CLIENT_SECRET is empty; edit the top of this script before running with -x"
        [[ -n "${WSO2_BASE_URL}" ]] || fatal "WSO2_BASE_URL is empty"
        [[ -n "${TOKEN_URL}"     ]] || fatal "TOKEN_URL is empty"
    fi

    log "Run id:            ${RUN_TS}"
    log "CSV:               ${CSV_FILE}"
    log "Gateway:           ${WSO2_BASE_URL}${API_PATH_PREFIX}"
    log "Municipality:      ${MUNICIPALITY_ID}"
    log "Mode:              $([[ "${DRY_RUN}" == "true" ]] && echo DRY-RUN || echo EXECUTE)"
    log "Key:               $([[ "${USE_EXTERNAL_REF}" == "true" ]] && echo "arrendekontrakt (lookup)" || echo "contractId (CSV)")"
    log "Throttle:          ${THROTTLE_MS} ms"
    log "Logs:              ${LOG_ROOT}"
}

# Extract a flat string value from a simple JSON object: "key":"value".
# Returns empty string if the key is absent. The `|| true` at the end prevents
# `set -e` from aborting when grep finds no match.
json_string() {
    local json="$1" key="$2"
    { printf '%s' "${json}" \
        | grep -oE "\"${key}\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" \
        | head -n 1 \
        | sed -E "s/.*\"${key}\"[[:space:]]*:[[:space:]]*\"([^\"]*)\"/\1/" ; } \
        || true
}

# Extract a flat numeric value from a simple JSON object: "key":123.
# Returns empty string if the key is absent.
json_number() {
    local json="$1" key="$2"
    { printf '%s' "${json}" \
        | grep -oE "\"${key}\"[[:space:]]*:[[:space:]]*-?[0-9]+" \
        | head -n 1 \
        | grep -oE -- '-?[0-9]+$' ; } \
        || true
}

fetch_token() {
    local resp http_code expires_in
    resp="$(curl -sS -w $'\n%{http_code}' \
        -u "${CLIENT_ID}:${CLIENT_SECRET}" \
        --data-urlencode "grant_type=client_credentials" \
        "${TOKEN_URL}" || true)"
    http_code="${resp##*$'\n'}"
    resp="${resp%$'\n'*}"
    [[ "${http_code}" =~ ^2 ]] \
        || fatal "Token request failed: HTTP ${http_code} ${resp:-<no response>}"

    ACCESS_TOKEN="$(json_string "${resp}" access_token)"
    expires_in="$(json_number "${resp}" expires_in)"
    [[ -n "${expires_in}" ]] || expires_in=3600
    [[ -n "${ACCESS_TOKEN}" ]] || fatal "No access_token in token response: ${resp}"
    # Force base-10 interpretation so a leading zero (e.g. "0300") is not
    # misread as octal (would error on "08"/"09" and abort under set -e).
    TOKEN_EXPIRES_AT=$(( $(date +%s) + 10#${expires_in} - 30 ))
    log "Obtained access token (expires in ${expires_in}s)"
}

ensure_token() {
    [[ "${DRY_RUN}" == "true" ]] && return 0
    if (( $(date +%s) >= TOKEN_EXPIRES_AT )); then
        log "Refreshing access token"
        fetch_token
    fi
}

# Build a PATCH body JSON given three (possibly empty) values.
# Values are known to be simple YYYY-MM-DD strings (no characters needing JSON
# escaping), so printf-based construction is safe.
build_body() {
    local end_date="$1" cp_start="$2" cp_end="$3"
    local parts=() cp_parts=() cp body

    [[ -n "${end_date}" ]] && parts+=("\"endDate\":\"${end_date}\"")

    [[ -n "${cp_start}" ]] && cp_parts+=("\"startDate\":\"${cp_start}\"")
    [[ -n "${cp_end}"   ]] && cp_parts+=("\"endDate\":\"${cp_end}\"")

    if (( ${#cp_parts[@]} > 0 )); then
        cp="$(IFS=,; printf '%s' "${cp_parts[*]}")"
        parts+=("\"currentPeriod\":{${cp}}")
    fi

    body="$(IFS=,; printf '%s' "${parts[*]}")"
    printf '{%s}' "${body}"
}

# Look up contractId by externalReferenceId. Caches into RESOLVED_CONTRACT_ID.
# Echoes the resolved contractId on stdout (empty if not resolvable).
resolve_contract_id() {
    local ext_ref="$1"
    if [[ -n "${RESOLVED_CONTRACT_ID[$ext_ref]:-}" ]]; then
        printf '%s' "${RESOLVED_CONTRACT_ID[$ext_ref]}"
        return 0
    fi

    ensure_token
    local url body http_status content_len resolved
    url="${WSO2_BASE_URL}${API_PATH_PREFIX}/${MUNICIPALITY_ID}/contracts"
    # curl --data-urlencode + -G appends properly-encoded query string
    body="$(curl -sS -G \
        -w $'\n%{http_code}' \
        -H "Authorization: Bearer ${ACCESS_TOKEN}" \
        -H "Accept: application/json" \
        --data-urlencode "filter=externalReferenceId:'${ext_ref}'" \
        --data-urlencode "size=2" \
        "${url}" || true)"
    http_status="${body##*$'\n'}"
    body="${body%$'\n'*}"

    if [[ "${http_status}" == "401" ]]; then
        log "  401 on lookup; refreshing token and retrying once"
        fetch_token
        body="$(curl -sS -G \
            -w $'\n%{http_code}' \
            -H "Authorization: Bearer ${ACCESS_TOKEN}" \
            -H "Accept: application/json" \
            --data-urlencode "filter=externalReferenceId:'${ext_ref}'" \
            --data-urlencode "size=2" \
            "${url}" || true)"
        http_status="${body##*$'\n'}"
        body="${body%$'\n'*}"
    fi

    if [[ "${http_status}" != "200" ]]; then
        log "  lookup for '${ext_ref}' failed: HTTP ${http_status} ${body}"
        printf ''
        return 1
    fi

    # Spring Page: "totalElements" = total number of matches across all pages.
    content_len="$(json_number "${body}" totalElements)"
    if [[ -z "${content_len}" || "${content_len}" == "0" ]]; then
        log "  lookup for '${ext_ref}' returned 0 contracts"
        printf ''
        return 1
    elif [[ "${content_len}" != "1" ]]; then
        log "  lookup for '${ext_ref}' returned ${content_len} contracts; ambiguous"
        printf ''
        return 1
    fi

    # First "contractId":"..." in body is content[0].contractId (Page JSON).
    resolved="$(json_string "${body}" contractId)"
    RESOLVED_CONTRACT_ID[$ext_ref]="${resolved}"
    printf '%s' "${resolved}"
}

# PATCH a single contract.
patch_contract() {
    local contract_id="$1" body="$2"
    local url http_status resp
    url="${WSO2_BASE_URL}${API_PATH_PREFIX}/${MUNICIPALITY_ID}/contracts/${contract_id}"

    resp="$(curl -sS -X PATCH \
        -w $'\n%{http_code}' \
        -H "Authorization: Bearer ${ACCESS_TOKEN}" \
        -H "Content-Type: application/json" \
        -d "${body}" \
        "${url}" || true)"
    http_status="${resp##*$'\n'}"
    resp="${resp%$'\n'*}"

    if [[ "${http_status}" == "401" ]]; then
        log "  401 on PATCH ${contract_id}; refreshing token and retrying once"
        fetch_token
        resp="$(curl -sS -X PATCH \
            -w $'\n%{http_code}' \
            -H "Authorization: Bearer ${ACCESS_TOKEN}" \
            -H "Content-Type: application/json" \
            -d "${body}" \
            "${url}" || true)"
        http_status="${resp##*$'\n'}"
        resp="${resp%$'\n'*}"
    fi

    logv "  HTTP ${http_status} body=${resp}"
    printf '%s' "${http_status}"
}

# Build one line per contract, pipe-delimited:
#   key|endDate|currentPeriod.startDate|currentPeriod.endDate
# Empty fields for dates not present in the CSV for that contract. The `|`
# delimiter is used (not TAB) so that bash `read` preserves empty fields —
# `IFS=$'\t'` would collapse consecutive tabs since TAB is whitespace-IFS.
# `|` is safe: contractIds (YYYY-NNNNN) and arrendekontrakt (digits/space/dash)
# do not contain it.
# Key = column B (contractId) unless USE_EXTERNAL_REF=true, then column A.
build_groups() {
    local key_col
    if [[ "${USE_EXTERNAL_REF}" == "true" ]]; then
        key_col='$1'
    else
        key_col='$2'
    fi

    awk -F',' -v keyexpr="${key_col}" '
        NR == 1 { next }
        /^$/    { next }
        {
            key   = (keyexpr == "$1") ? $1 : $2
            field = $3
            value = $4
            if (key == "" || field == "" || value == "") next
            if (field != "endDate" && field != "currentPeriod.startDate" && field != "currentPeriod.endDate") next
            print key "\t" field "\t" value
        }
    ' "${NORM_CSV}" \
    | sort -t $'\t' -k1,1 \
    | awk -F'\t' '
        function flush() {
            if (prev != "") {
                printf("%s|%s|%s|%s\n", prev, ed, cps, cpe)
            }
            ed=""; cps=""; cpe=""
        }
        {
            if ($1 != prev) { flush(); prev = $1 }
            if ($2 == "endDate")                      ed  = $3
            else if ($2 == "currentPeriod.startDate") cps = $3
            else if ($2 == "currentPeriod.endDate")   cpe = $3
        }
        END { flush() }
    '
}

main() {
    parse_args "$@"
    preflight

    if [[ "${DRY_RUN}" != "true" ]]; then
        fetch_token
    fi

    local payloads_ndjson="${LOG_ROOT}/payloads.ndjson"
    local failures_log="${LOG_ROOT}/failures.log"
    : > "${payloads_ndjson}"
    : > "${failures_log}"

    local groups_file="${LOG_ROOT}/groups.tsv"
    build_groups > "${groups_file}"
    TOTAL="$(wc -l < "${groups_file}" | tr -d ' ')"
    log "Contracts to patch: ${TOTAL}"

    local key ed cps cpe body contract_id http_status
    local line_no=0
    while IFS='|' read -r key ed cps cpe; do
        line_no=$(( line_no + 1 ))
        [[ -z "${key}" ]] && continue

        body="$(build_body "${ed}" "${cps}" "${cpe}")"

        # Resolve contractId
        if [[ "${USE_EXTERNAL_REF}" == "true" ]]; then
            if [[ "${DRY_RUN}" == "true" ]]; then
                contract_id="(to-be-resolved-from:${key})"
            else
                contract_id="$(resolve_contract_id "${key}" || true)"
                if [[ -z "${contract_id}" ]]; then
                    SKIP_COUNT=$(( SKIP_COUNT + 1 ))
                    printf '%s\tSKIP_NO_CONTRACT_ID\texternalRef=%s\n' "${key}" "${key}" >> "${failures_log}"
                    continue
                fi
            fi
        else
            contract_id="${key}"
        fi

        # key (arrendekontrakt) and contractId contain only digits, spaces and
        # dashes — safe to embed in JSON strings without further escaping.
        printf '{"key":"%s","contractId":"%s","body":%s}\n' \
            "${key}" "${contract_id}" "${body}" >> "${payloads_ndjson}"

        if [[ "${DRY_RUN}" == "true" ]]; then
            logv "[${line_no}/${TOTAL}] DRY-RUN PATCH ${MUNICIPALITY_ID}/contracts/${contract_id} body=${body}"
            OK_COUNT=$(( OK_COUNT + 1 ))
            continue
        fi

        ensure_token
        http_status="$(patch_contract "${contract_id}" "${body}")"
        if [[ "${http_status}" =~ ^2 ]]; then
            OK_COUNT=$(( OK_COUNT + 1 ))
            log "[${line_no}/${TOTAL}] OK ${contract_id} body=${body}"
        else
            FAIL_COUNT=$(( FAIL_COUNT + 1 ))
            log "[${line_no}/${TOTAL}] FAIL ${contract_id} http=${http_status}"
            printf '%s\tHTTP_%s\t%s\n' "${contract_id}" "${http_status}" "${body}" >> "${failures_log}"
        fi

        if (( 10#${THROTTLE_MS} > 0 )); then
            sleep "$(awk -v ms="${THROTTLE_MS}" 'BEGIN{printf "%.3f", ms/1000}')"
        fi
    done < "${groups_file}"

    log "-----"
    log "Mode:      $([[ "${DRY_RUN}" == "true" ]] && echo DRY-RUN || echo EXECUTE)"
    log "Total:     ${TOTAL}"
    log "Succeeded: ${OK_COUNT}"
    log "Failed:    ${FAIL_COUNT}"
    log "Skipped:   ${SKIP_COUNT}"
    log "Payloads:  ${payloads_ndjson}"
    log "Failures:  ${failures_log}"

    (( FAIL_COUNT == 0 )) || exit 1
}

main "$@"
