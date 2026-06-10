# Changelog

All notable changes to the Contracts API are documented here.

## 10.0 — 2026-06-08

### Breaking changes (contract versioning removed)

Contracts are no longer versioned — each contract is now a single, mutable record instead of an append-only chain of
versions. The endpoints for creating, reading, updating, patching and deleting a contract are otherwise unchanged.

- **`version` removed from responses** — the read-only `version` field is gone from every contract payload
  (`GET /{municipalityId}/contracts` and `GET /{municipalityId}/contracts/{contractId}`).
- **`?version=` query parameter removed** — `GET /{municipalityId}/contracts/{contractId}` no longer accepts a
  `version` parameter; it always returns the current contract. (Previous versions are no longer retained.)
- **`POST /{municipalityId}/contracts/{contractId}/diff` removed** — version comparison no longer exists.
- **`PUT` updates in place** — `PUT /{municipalityId}/contracts/{contractId}` now replaces the existing contract
  instead of creating a new version. `PATCH` continues to apply a partial update in place. Both keep the same
  `contractId`.
- **Contract `type` is now mutable** — both `PUT` and `PATCH` can change a contract's `type`. Previously the type was
  fixed after creation (a `PATCH` carrying a new `type` was silently ignored). Changing the type re-runs the
  type-specific business rules, which may remove attributes not applicable to the new type.

### Data / operations

- Migration `V1_17` collapses each contract to its latest version (older versions and their child rows are deleted —
  **irreversible**), drops the `version` column and the `(contract_id, version)` unique constraint, and adds a
  `(municipality_id, contract_id)` unique constraint.

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

