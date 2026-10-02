-- Populate billing_enabled (invoicing.billingEnabled in the API) on existing contracts.
--
-- 1. Only contracts that have invoicing details (invoice_interval or invoiced_in set) are touched. Contracts without
--    invoicing keep billing_enabled null, so that the invoicing embeddable still reads back as absent for them.
--
-- 2. A contract with invoicing details is billed if it has a yearly fee greater than 0. Note that fee_yearly = 0 does
--    not count as a value here: V1_17 defaulted every null fee_yearly to 0, so a zero is indistinguishable from a fee
--    that was never set. All other contracts with invoicing details are not billed.
--
-- 3. Contract 2026-06290 is explicitly not billed, regardless of the above.

update contract
set billing_enabled = case when fee_yearly > 0 then 1 else 0 end
where invoice_interval is not null
   or invoiced_in is not null;

update contract
set billing_enabled = 0
where contract_id = '2026-06290';
