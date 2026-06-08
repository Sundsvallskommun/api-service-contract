# Changelog

All notable changes to the Contracts API are documented here.

## 10.0 — 2026-06-04

### Breaking changes (endpoint removed)

- **`PUT /{municipalityId}/contracts/{contractId}`** ("Update a contract") has been **removed**. It was the
  only operation that created a new contract version; `POST` creates version 1 and `PATCH` updates the
  latest version in place. Clients that updated contracts via `PUT` must switch to
  **`PATCH /{municipalityId}/contracts/{contractId}`**, which applies the non-null fields from the payload
  onto the existing contract (no new version is created) and emits the same `CONTRACT_UPDATED` billing event.
- The contract versioning columns and the `POST /{municipalityId}/contracts/{contractId}/diff` endpoint are
  retained and remain functional; with `PUT` gone, contracts created and patched through the API stay at
  version 1.

### Other behavioural changes

- **`PATCH` now supports clearing fields (JSON Merge Patch semantics).** A field that is **omitted** from the
  payload is left unchanged, a field set to **`null`** is now **cleared**, and a field set to a value is updated.
  Previously a `null` was indistinguishable from an omitted field and was ignored, so there was no way to clear a
  value. (`signedByWitness` is a primitive boolean and cannot be cleared; an explicit `null` for it is ignored.)
- **`PATCH` merges nested objects recursively.** The same omit/null/value rules apply at every level of `fees`,
  `invoicing`, `leasehold`, `notice`, `extension` and `currentPeriod` — so e.g. `{"fees": {"monthly": 500}}` now
  updates only `monthly` and **keeps** the other fee fields. Previously these objects were replaced as a whole, so
  any sub-field left out was cleared. Arrays (e.g. `stakeholders`, `propertyDesignations`, `additionalInformation`)
  are still replaced as a whole when provided.
- **Cross-field invariants are now enforced on the resulting contract** rather than on the request model: the fee
  index trio consistency, the "`autoExtend` requires `leaseExtension` + `unit`" rule, and "invoicing must have both
  interval and `invoicedIn`". The same requests are accepted/rejected as before, but the rules now hold for the
  merged result of a `PATCH` as well as for a create.
- **Contract `type` is now updatable via `PATCH`.** The `type` column was previously mapped non-updatable, so
  the type could only change as a side effect of `PUT` creating a new version. With `PUT` removed, the
  non-updatable mapping was dropped so a `PATCH` that includes `type` changes it in place (and the
  purchase-agreement business rule is re-evaluated accordingly).

## 9.0 — 2026-06-01

### Breaking changes (request validation tightened)

Requests that were previously accepted may now be rejected with **HTTP 400 Bad Request**. The request/response
**schema is unchanged** — only the set of accepted values has been narrowed. These contracts were producing
failed billing downstream (BillingPreprocessor `400` / open circuit breaker); they are now rejected at creation,
update and patch instead.

New constraints on `POST`, `PUT` and `PATCH /{municipalityId}/contracts`:

- **`fees.additionalInformation`** — each entry must be non-blank and between **1 and 30** characters
  (`@NotBlank` + `@Size(min = 1, max = 30)`). Used as invoice row descriptions downstream.
- **Fee index fields** — `fees.indexType`, `fees.indexYear` and `fees.indexNumber` are all-or-nothing: if any is
  set, all must be set and `indexNumber` must be greater than `0`.
- **`PRIMARY_BILLING_PARTY`** — when both `invoicing.invoiceInterval` and `invoicing.invoicedIn` are set, at least
  one stakeholder must have the `PRIMARY_BILLING_PARTY` role, and that billing party must have a usable recipient
  name (an organization name, or both a first and last name) — otherwise billing rejects the record with
  "recipient must either have an organization name or a first and last name defined".
- **`propertyDesignations`** — a designation that is sent in must have a real `name`: a whitespace-only name is
  rejected, an empty-string name is silently dropped (no element persisted), and a missing/`null` name is likewise
  dropped. Designations are never required, and there is no longer any requirement tied to `leaseType`.
- **`endDate`** — may not be **set or changed** to a date before today (today/future/`null` allowed). An unchanged
  endDate that is already in the past is still accepted, so contracts whose endDate has already passed (e.g.
  terminated ones) can still be updated.

### Other behavioural changes

- Blank strings are normalized on persist: a blank `externalReferenceId` and a blank `fees.indexType` are stored
  as `null`, and blank elements in `leasehold.additionalInformation` are dropped. Property designations with an
  empty or missing `name` are likewise dropped so that no blank designation rows are persisted.

### Data / operations

- Migration `V1_16` normalizes existing blank data (blank `external_reference_id` → `null`; blank
  `fee_additional_information` / `additional_information` rows removed).
- Existing contracts that violate the new rules but cannot be auto-corrected (missing billing party, partial index,
  recipient missing name/address, over-long descriptions) must be identified and corrected manually in test and
  production with domain knowledge.

