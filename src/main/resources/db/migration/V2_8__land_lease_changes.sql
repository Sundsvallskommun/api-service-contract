alter table land_lease_contract
    drop column property_designation;

create table land_lease_contract_property_designations (
    contract_id           varchar(10) not null,
    property_designations varchar(255)
);

alter table if exists land_lease_contract_property_designations
    add constraint fk_land_lease_contract_property_designations_contract_id
        foreign key (contract_id)
            references land_lease_contract (id);

create index idx_land_lease_contract_property_designations_contract_id
    on land_lease_contract_property_designations (contract_id);

create table land_lease_contract_leasehold_additional_information (
    additional_information varchar(255),
    land_lease_contract_id varchar(10) not null
);

alter table if exists land_lease_contract_leasehold_additional_information
    add constraint fk_llc_leasehold_additional_information_land_lease_contract_id
        foreign key (land_lease_contract_id)
            references land_lease_contract (id);
