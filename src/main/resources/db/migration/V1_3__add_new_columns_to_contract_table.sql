alter table if exists contract
    add column if not exists lease_duration_unit varchar(32);

alter table if exists contract
    add column if not exists lease_extension_unit varchar(32);

alter table if exists contract
    drop column if exists period_of_notice;
    
alter table if exists contract
    drop column if exists usufruct_type;    
    
alter table if exists contract
    rename column land_lease_type to lease_type;

create table if not exists contract_notice (
    period_of_notice integer not null,
    contract_id bigint not null,
    unit varchar(32) not null,
    party varchar(255) not null
) engine=InnoDB;

alter table if exists contract_notice 
    add constraint fk_contract_notice_contract_id 
    foreign key (contract_id) 
    references contract (id);
