-- Add the month in which yearly billing takes place (invoicing.yearlyBillingMonth in the API): 6 (June) or 12
-- (December). It is mandatory for contracts with invoice interval YEARLY, which is enforced by the API.
--
-- The column is nullable on purpose: it is part of the invoicing embeddable and has no meaning for other intervals.

alter table contract
    add column if not exists yearly_billing_month int;
