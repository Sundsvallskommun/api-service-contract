-- Add a flag controlling whether a contract is to be billed or not (invoicing.billingEnabled in the API).
--
-- The column is nullable on purpose: it is part of the invoicing embeddable, and a contract without invoicing details
-- must keep all of its invoicing columns null so that the embeddable reads back as absent.
--
-- Existing contracts are given a value in V1_21__populate_billing_enabled_on_contract.sql.

alter table contract
    add column if not exists billing_enabled bit;
