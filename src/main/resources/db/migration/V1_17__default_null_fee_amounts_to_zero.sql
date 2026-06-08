-- Default null yearly/monthly fee amounts to 0 on existing contracts. A null amount breaks the downstream billing
-- pipeline (BillingDataCollector), where the cost calculation fails fast with "missing crucial information for
-- calculating indexed cost" and the billing scheduler retries the same contract on every tick. Going forward this is
-- prevented at create/update/patch time: when a fees object is sent, a null yearly/monthly is normalized to 0 in
-- EntityMapper.toFeesEmbeddable. Existing rows are corrected here so the already-stored data stops tripping billing.

update contract
set fee_yearly = 0
where fee_yearly is null;

update contract
set fee_monthly = 0
where fee_monthly is null;
