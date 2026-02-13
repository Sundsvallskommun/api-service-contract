
    create table additional_information (
        contract_id bigint not null,
        additional_information varchar(255)
    ) engine=InnoDB;

    create table attachment (
        municipality_id varchar(4),
        created datetime(6),
        id bigint not null auto_increment,
        contract_id varchar(10) not null,
        category varchar(64),
        filename varchar(255),
        mime_type varchar(255),
        note varchar(255),
        content LONGBLOB,
        primary key (id)
    ) engine=InnoDB;

    create table contract (
        area integer,
        auto_extend bit,
        current_period_end_date date,
        current_period_start_date date,
        end_date date,
        fee_index_number integer,
        fee_index_year integer,
        fee_indexation_rate decimal(38,2),
        fee_monthly decimal(38,2),
        fee_total decimal(38,2),
        fee_yearly decimal(38,2),
        lease_duration integer,
        lease_extension integer,
        municipality_id varchar(4),
        notice_date date,
        signed_by_witness bit,
        start_date date,
        version integer,
        id bigint not null auto_increment,
        contract_id varchar(10) not null,
        lease_duration_unit varchar(32),
        lease_extension_unit varchar(32),
        invoice_interval varchar(64),
        invoiced_in varchar(64),
        lease_type varchar(64),
        leasehold_type varchar(64),
        notice_given_by varchar(64),
        status varchar(64),
        type varchar(64),
        description varchar(4096),
        external_reference_id varchar(255),
        fee_currency varchar(255),
        fee_index_type varchar(255),
        fee_total_as_text varchar(255),
        leasehold_description varchar(255),
        object_identity varchar(255),
        area_data longblob,
        primary key (id)
    ) engine=InnoDB;

    create table contract_notice (
        period_of_notice integer not null,
        contract_id bigint not null,
        unit varchar(32) not null,
        party varchar(64) not null
    ) engine=InnoDB;

    create table contract_stakeholder (
        contract_id bigint not null,
        stakeholder_id bigint not null
    ) engine=InnoDB;

    create table extra_parameter (
        extra_parameter_group_id bigint not null,
        parameter_key varchar(255) not null,
        parameter_value varchar(255),
        primary key (extra_parameter_group_id, parameter_key)
    ) engine=InnoDB;

    create table extra_parameter_group (
        contract_id bigint,
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table fee_additional_information (
        contract_id bigint not null,
        additional_information varchar(255)
    ) engine=InnoDB;

    create table property_designation (
        contract_id bigint not null,
        district varchar(255),
        name varchar(255)
    ) engine=InnoDB;

    create table stakeholder (
        id bigint not null auto_increment,
        address_type varchar(64),
        type varchar(64),
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

    create table term_group (
        contract_id bigint,
        id bigint not null auto_increment,
        term_type varchar(32),
        header varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table term_group_term (
        term_group_id bigint not null,
        description varchar(255),
        term_name varchar(255)
    ) engine=InnoDB;

    create index idx_attachment_municipality_id_contract_id 
       on attachment (municipality_id, contract_id);

    create index idx_contract_municipality_id_contract_id 
       on contract (municipality_id, contract_id);

    alter table if exists contract 
       add constraint uq_contract_contract_id_version unique (contract_id, version);

    alter table if exists contract_stakeholder 
       add constraint uq_contract_stakeholder_stakeholder_id unique (stakeholder_id);

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
       add constraint fk_contract_stakeholder_stakeholder_id 
       foreign key (stakeholder_id) 
       references stakeholder (id);

    alter table if exists contract_stakeholder 
       add constraint fk_contract_stakeholder_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists extra_parameter 
       add constraint fk_extra_parameter_extra_parameter_group_id 
       foreign key (extra_parameter_group_id) 
       references extra_parameter_group (id);

    alter table if exists extra_parameter_group 
       add constraint fk_extra_parameter_group_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists fee_additional_information 
       add constraint fk_fee_additional_information_contract_id 
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

    alter table if exists term_group 
       add constraint fk_term_group_contract_id 
       foreign key (contract_id) 
       references contract (id);

    alter table if exists term_group_term 
       add constraint fk_term_group_term_term_group_id 
       foreign key (term_group_id) 
       references term_group (id);
