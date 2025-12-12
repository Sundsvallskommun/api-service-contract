
    create table additional_information (
        contract_id bigint not null,
        additional_information varchar(255)
    ) engine=InnoDB;

    create table attachment (
        municipality_id varchar(4),
        id bigint not null auto_increment,
        contract_id varchar(10) not null,
        category varchar(255),
        filename varchar(255),
        mime_type varchar(255),
        note varchar(255),
        content LONGBLOB,
        primary key (id)
    ) engine=InnoDB;

    create table contract (
        area integer,
        auto_extend bit,
        end date,
        lease_duration integer,
        lease_extension integer,
        municipality_id varchar(4),
        signed_by_witness bit,
        start date,
        version integer,
        id bigint not null auto_increment,
        contract_id varchar(10) not null,
        lease_duration_unit varchar(32),
        lease_extension_unit varchar(32),
        fees varchar(2048),
        description varchar(4096),
        additional_terms varchar(255),
        external_reference_id varchar(255),
        extra_parameters varchar(255),
        index_terms varchar(255),
        invoice_interval varchar(255),
        invoiced_in varchar(255),
        lease_type varchar(255),
        leasehold_description varchar(255),
        leasehold_type varchar(255),
        object_identity varchar(255),
        status varchar(255),
        type varchar(255),
        area_data longblob,
        primary key (id)
    ) engine=InnoDB;

    create table contract_notice (
        period_of_notice integer not null,
        contract_id bigint not null,
        unit varchar(32) not null,
        party varchar(255) not null
    ) engine=InnoDB;

    create table contract_stakeholder (
        contract_id bigint not null,
        stakeholder_id bigint not null
    ) engine=InnoDB;

    create table property_designation (
        contract_id bigint not null,
        district varchar(255),
        name varchar(255)
    ) engine=InnoDB;

    create table stakeholder (
        id bigint not null auto_increment,
        address_type varchar(255),
        attention varchar(255),
        care_of varchar(255),
        country varchar(255),
        email_address varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        organization_name varchar(255),
        organization_number varchar(255),
        party_id varchar(255),
        phone_number varchar(255),
        postal_code varchar(255),
        roles varchar(255),
        street_address varchar(255),
        town varchar(255),
        type varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table stakeholder_parameter (
        parameter_order integer default 0 not null,
        id bigint not null auto_increment,
        stakeholder_id bigint not null,
        display_name varchar(255),
        parameters_key varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table stakeholder_parameter_values (
        stakeholder_parameter_id bigint not null,
        value varchar(255)
    ) engine=InnoDB;

    alter table if exists contract 
       add constraint UKm3quweshowxagqqh3pim6psrn unique (contract_id, version);

    alter table if exists contract_stakeholder 
       add constraint UKrx3mnf3457nemo7pg9k5vy68e unique (stakeholder_id);

    create index idx_contract_property_designation_contract_id 
       on property_designation (contract_id);

    alter table if exists additional_information 
       add constraint fk_additional_information_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists contract_notice 
       add constraint fk_contract_notice_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists contract_stakeholder 
       add constraint FKhpe1qw09qkohkltb0v73jmr2d 
       foreign key (stakeholder_id) 
       references stakeholder (id);

    alter table if exists contract_stakeholder 
       add constraint FKk7r9w3gvreq7n6kxigo6pisbp 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists property_designation 
       add constraint fk_contract_property_designation_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists stakeholder_parameter 
       add constraint fk_stakeholder_parameter_stakeholder_id 
       foreign key (stakeholder_id) 
       references stakeholder (id);

    alter table if exists stakeholder_parameter_values 
       add constraint fk_stakeholder_parameter_values_stakeholder_parameter_id 
       foreign key (stakeholder_parameter_id) 
       references stakeholder_parameter (id);
