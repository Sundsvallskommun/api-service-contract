-- Add current period and notice fields to contract
ALTER TABLE contract ADD COLUMN IF NOT EXISTS current_period_start_date date;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS current_period_end_date date;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS notice_date date;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS notice_given_by varchar(64);

-- Remove notice_date from contract_notice (moved to contract level)
ALTER TABLE contract_notice DROP COLUMN IF EXISTS notice_date;

-- Add created timestamp to attachment
ALTER TABLE attachment ADD COLUMN IF NOT EXISTS created datetime(6);
