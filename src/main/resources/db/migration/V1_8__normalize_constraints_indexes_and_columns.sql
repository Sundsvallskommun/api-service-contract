-- Drop any auto-generated unique constraints (from Hibernate DDL)
alter table contract drop index if exists UKm3quweshowxagqqh3pim6psrn;
alter table contract_stakeholder drop index if exists UKrx3mnf3457nemo7pg9k5vy68e;

-- Drop any auto-generated foreign keys (from Hibernate DDL)
alter table contract_stakeholder drop foreign key if exists FKhpe1qw09qkohkltb0v73jmr2d;
alter table contract_stakeholder drop foreign key if exists FKk7r9w3gvreq7n6kxigo6pisbp;

-- Drop old foreign key name from V1_0
alter table contract_stakeholder drop foreign key if exists fk_contract_stakeholder_contract_entity_id;

-- Ensure correct unique constraints exist
create unique index if not exists uq_contract_contract_id_version on contract (contract_id, version);
create unique index if not exists uq_contract_stakeholder_stakeholder_id on contract_stakeholder (stakeholder_id);

-- Recreate foreign keys with correct names
alter table contract_stakeholder drop foreign key if exists fk_contract_stakeholder_stakeholder_id;
alter table contract_stakeholder add constraint fk_contract_stakeholder_stakeholder_id foreign key (stakeholder_id) references stakeholder(id);

alter table contract_stakeholder drop foreign key if exists fk_contract_stakeholder_contract_id;
alter table contract_stakeholder add constraint fk_contract_stakeholder_contract_id foreign key (contract_id) references contract(id);

-- Add composite indexes for frequently queried columns
create index if not exists idx_contract_municipality_id_contract_id on contract (municipality_id, contract_id);
create index if not exists idx_attachment_municipality_id_contract_id on attachment (municipality_id, contract_id);

-- Optimize enum column lengths
alter table contract modify column if exists status varchar(64);
alter table contract modify column if exists type varchar(64);
alter table contract modify column if exists lease_type varchar(64);
alter table contract modify column if exists invoice_interval varchar(64);
alter table contract modify column if exists invoiced_in varchar(64);
alter table contract modify column if exists leasehold_type varchar(64);
alter table contract_notice modify column if exists party varchar(64) not null;
alter table stakeholder modify column if exists address_type varchar(64);
alter table stakeholder modify column if exists type varchar(64);
alter table attachment modify column if exists category varchar(64);

-- Rename reserved SQL keywords used as column names
alter table contract change column if exists `start` start_date date;
alter table contract change column if exists `end` end_date date;
