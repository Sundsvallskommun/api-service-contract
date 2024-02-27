
    create table attachment (
        id bigint not null auto_increment,
        extension varchar(255),
        file varchar(255),
        mime_type varchar(255),
        name varchar(255),
        note varchar(255),
        category enum ('KONTRAKT','OTHER'),
        primary key (id)
    ) engine=InnoDB;

    create table contract (
        signed_by_witness bit,
        version integer,
        case_id bigint,
        id bigint not null auto_increment,
        additional_terms varchar(255),
        description varchar(255),
        index_terms varchar(255),
        municipality_id varchar(255),
        status enum ('ACTIVE','TERMINATED'),
        primary key (id)
    ) engine=InnoDB;

    create table contract_attachments (
        attachments_id bigint not null,
        contract_entity_id bigint not null
    ) engine=InnoDB;

    create table contract_stakeholders (
        contract_entity_id bigint not null,
        stakeholders_id bigint not null
    ) engine=InnoDB;

    create table land_lease_contract (
        area integer,
        auto_extend bit,
        end date,
        lease_duration integer,
        lease_extension integer,
        period_of_notice integer,
        rental decimal(38,2),
        start date,
        id bigint not null,
        external_reference_id varchar(255),
        leasehold_description varchar(255),
        object_identity varchar(255),
        property_designation varchar(255),
        area_data longblob,
        invoice_interval enum ('YEARLY','QUARTERLY','MONTHLY'),
        land_lease_type enum ('LEASEHOLD','USUFRUCT','SITELEASEHOLD'),
        leasehold_type enum ('APARTMENT','BUILDING','AGRICULTURE','DWELLING','OTHER'),
        usufruct_type enum ('HUNTING','FISHING','MAINTENANCE','OTHER'),
        primary key (id)
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
        role tinyint check (role between 0 and 4),
        stakeholder_entity_id bigint not null
    ) engine=InnoDB;

    alter table if exists contract_attachments
       add constraint UK_qkj7mvq9plqt57iwee0cxlxup unique (attachments_id);

    alter table if exists contract_stakeholders
       add constraint UK_qac1fekbip0grswuir26yee9n unique (stakeholders_id);

    alter table if exists contract_attachments
       add constraint FKbp5d0qikkbinyods0otskow8s
       foreign key (attachments_id)
       references attachment (id);

    alter table if exists contract_attachments
       add constraint FKkcm2xjf6opwjey4a6ufg6pofi
       foreign key (contract_entity_id)
       references contract (id);

    alter table if exists contract_stakeholders
       add constraint FKmn0g63682mfnqlbx9oyotpotl
       foreign key (stakeholders_id)
       references stakeholder (id);

    alter table if exists contract_stakeholders
       add constraint FKbc8lss007ect2hti8u26lncf
       foreign key (contract_entity_id)
       references contract (id);

    alter table if exists land_lease_contract
       add constraint FKpbtxseyi9dvh0u745mfc0h2om
       foreign key (id)
       references contract (id);

    alter table if exists stakeholder_roles
       add constraint FK62k800nm3s35rdae4e07v6f10
       foreign key (stakeholder_entity_id)
       references stakeholder (id);
