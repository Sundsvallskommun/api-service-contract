-- Normalize blank string data that the API historically accepted and that breaks the downstream billing
-- pipeline (BillingDataCollector / BillingPreprocessor). Going forward this is prevented at create/update/patch
-- time: blank leasehold list elements are filtered, a blank external_reference_id is stored as null, and fee
-- additional information must be non-blank. Existing rows are cleaned here so the billing circuit breaker stops
-- tripping on already-stored data.
--
-- This migration only performs SAFE, business-meaning-preserving normalization. Violations that cannot be fixed
-- without domain knowledge (missing PRIMARY_BILLING_PARTY, partial fee index data, missing property designations,
-- recipients lacking name/address, over-30-character descriptions) are NOT touched here and must be identified and
-- corrected manually with domain knowledge.

-- Remove blank fee additional-information entries (used as invoice row descriptions downstream).
delete from fee_additional_information
where additional_information is null
   or length(trim(additional_information)) = 0;

-- Remove blank leasehold additional-information entries.
delete from additional_information
where additional_information is null
   or length(trim(additional_information)) = 0;

-- Store blank external reference ids as null instead of empty strings.
update contract
set external_reference_id = null
where external_reference_id is not null
  and length(trim(external_reference_id)) = 0;
