alter table if exists land_lease_contract drop constraint if exists fk_land_lease_contract_contract_id;
alter table if exists contract_attachments drop constraint if exists fk_contract_attachments_contract_entity_id;
alter table if exists contract_stakeholders drop constraint if exists fk_contract_stakeholders_stakeholders_id;

alter table if exists contract modify column id varchar(10) not null;

alter table if exists land_lease_contract
    modify column id varchar(10) not null;
alter table if exists land_lease_contract
    add constraint fk_land_lease_contract_contract_id
        foreign key (id)
            references contract (id);

alter table if exists contract_attachments
    modify column contract_entity_id varchar(10) not null;
alter table if exists contract_attachments
    add constraint fk_contract_attachments_contract_entity_id
        foreign key (contract_entity_id)
            references contract (id);

alter table contract_stakeholders
    modify column contract_entity_id varchar(10) not null;
alter table if exists contract_stakeholders
    add constraint fk_contract_stakeholders_stakeholders_id
        foreign key (contract_entity_id)
            references contract (id);
