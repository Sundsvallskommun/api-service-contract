
create table attachment (
    category enum ('CONTRACT','OTHER'),
    extension varchar(255),
    file varchar(255),
    id bigint not null auto_increment,
    mime_type varchar(255),
    name varchar(255),
    note varchar(255),
    primary key (id)
) engine=InnoDB;

create table contract (
    additional_terms varchar(255),
    case_id bigint,
    contract_id varchar(10),
    description varchar(4096),
    id bigint not null auto_increment,
    index_terms varchar(255),
    municipality_id varchar(255),
    signed_by_witness bit,
    status enum ('ACTIVE','DRAFT','TERMINATED'),
    version integer,
    primary key (id)
) engine=InnoDB;

create table contract_attachments (
    attachments_id bigint not null,
    contract_entity_id bigint not null
) engine=InnoDB;

create table contract_extra_parameter (
    contract_id bigint not null,
    parameter_key varchar(255) not null,
    parameter_value varchar(255) not null,
    primary key (contract_id, parameter_key)
) engine=InnoDB;

create table contract_stakeholders (
    stakeholders_id bigint not null,
    contract_entity_id bigint not null
) engine=InnoDB;

create table land_lease_contract (
    area integer,
    auto_extend bit,
    end date,
    lease_duration integer,
    lease_extension integer,
    period_of_notice integer,
    start date,
    external_reference_id varchar(255),
    id bigint not null,
    lease_fees varchar(255),
    leasehold_description varchar(255),
    object_identity varchar(255),
    area_data longblob,
    invoice_interval enum ('YEARLY','QUARTERLY','MONTHLY'),
    invoiced_in enum ('ADVANCE','ARREARS'),
    land_lease_type enum ('LEASEHOLD','USUFRUCT','SITELEASEHOLD'),
    leasehold_type enum ('AGRICULTURE','APARTMENT','BUILDING','DWELLING','OTHER'),
    usufruct_type enum ('HUNTING','FISHING','MAINTENANCE','OTHER'),
    primary key (id)
) engine=InnoDB;

create table land_lease_contract_leasehold_additional_information (
    additional_information varchar(255),
    land_lease_contract_id bigint not null
) engine=InnoDB;

create table land_lease_contract_property_designations (
    contract_id bigint not null,
    property_designations varchar(255)
) engine=InnoDB;

create table stakeholder (
    id bigint not null auto_increment,
    attention varchar(255),
    country varchar(255),
    email_address varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    organization_name varchar(255),
    organization_number varchar(255),
    person_id varchar(255),
    phone_number varchar(255),
    postal_code varchar(255),
    street_address varchar(255),
    town varchar(255),
    address_type enum ('POSTAL_ADDRESS','BILLING_ADDRESS','VISITING_ADDRESS'),
    type enum ('PERSON','COMPANY','ASSOCIATION'),
    primary key (id)
) engine=InnoDB;

create table stakeholder_roles (
    stakeholder_entity_id bigint not null,
    role enum ('BUYER','CONTACT_PERSON','GRANTOR','LAND_OWNER','LEASE_HOLDER','POWER_OF_ATTORNEY_CHECK','POWER_OF_ATTORNEY_ROLE','SELLER','SIGNATORY')
) engine=InnoDB;

alter table if exists contract
    add constraint uq_contract_contract_id_version unique (contract_id, version);

alter table if exists contract_attachments
    add constraint uq_contract_attachments_attachments_id unique (attachments_id);

create index idx_extra_parameter_contract_id
    on contract_extra_parameter (contract_id);

alter table if exists contract_stakeholders
    add constraint uq_contract_stakeholders_stakeholders_id unique (stakeholders_id);

create index idx_land_lease_contract_property_designations_contract_id
    on land_lease_contract_property_designations (contract_id);

alter table if exists contract_attachments
    add constraint fk_contract_attachments_attachments_id
        foreign key (attachments_id)
            references attachment (id);

alter table if exists contract_attachments
    add constraint fk_contract_attachments_contract_entity_id
        foreign key (contract_entity_id)
            references contract (id);

alter table if exists contract_extra_parameter
    add constraint fk_extra_parameter_contract_id
        foreign key (contract_id)
            references contract (id);

alter table if exists contract_stakeholders
    add constraint fk_contract_stakeholders_stakeholder_id
        foreign key (stakeholders_id)
            references stakeholder (id);

alter table if exists contract_stakeholders
    add constraint fk_contract_stakeholders_contract_entity_id
        foreign key (contract_entity_id)
            references contract (id);

alter table if exists land_lease_contract
    add constraint fk_land_lease_contract_contract_id
        foreign key (id)
            references contract (id);

alter table if exists land_lease_contract_leasehold_additional_information
    add constraint fk_llc_leasehold_additional_information_land_lease_contract_id
        foreign key (land_lease_contract_id)
            references land_lease_contract (id);

alter table if exists land_lease_contract_property_designations
    add constraint fk_land_lease_contract_property_designations_contract_id
        foreign key (contract_id)
            references land_lease_contract (id);

alter table if exists stakeholder_roles
    add constraint fk_stakeholder_roles_stakeholder_entity_id
        foreign key (stakeholder_entity_id)
            references stakeholder (id);

CREATE SEQUENCE IF NOT EXISTS `contract_id_seq` START WITH 1 INCREMENT BY 1;
