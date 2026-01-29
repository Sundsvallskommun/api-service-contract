alter table if exists contract_notice
    add column if not exists notice_date date;
