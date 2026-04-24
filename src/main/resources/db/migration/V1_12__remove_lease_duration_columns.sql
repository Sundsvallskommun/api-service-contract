alter table if exists contract
    drop column if exists lease_duration;

alter table if exists contract
    drop column if exists lease_duration_unit;
