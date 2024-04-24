create sequence if not exists contract_id_seq start with 1 increment by 1;

create table attachment
(
    category        varchar(255),
    content         longblob,
    contract_id     varchar(10) not null,
    filename        varchar(255),
    id              bigint      not null auto_increment,
    mime_type       varchar(255),
    municipality_id varchar(4),
    note            varchar(255),
    primary key (id)
) engine = InnoDB;

create table contract
(
    additional_terms      json,
    area                  integer,
    area_data             longblob,
    auto_extend           bit,
    contract_id           varchar(10) not null,
    description           varchar(4096),
    end                   date,
    external_reference_id varchar(255),
    extra_parameters      json,
    fees                  varchar(2048),
    id                    bigint      not null auto_increment,
    index_terms           json,
    invoice_interval      varchar(255),
    invoiced_in           varchar(255),
    land_lease_type       varchar(255),
    lease_duration        integer,
    lease_extension       integer,
    leasehold_description varchar(255),
    leasehold_type        varchar(255),
    municipality_id       varchar(4),
    object_identity       varchar(255),
    period_of_notice      integer,
    signed_by_witness     bit,
    start                 date,
    status                varchar(255),
    type                  varchar(255),
    usufruct_type         varchar(255),
    version               integer,
    primary key (id)
) engine = InnoDB;

create table contract_stakeholder
(
    contract_id    bigint not null,
    stakeholder_id bigint not null
) engine = InnoDB;

create table additional_information
(
    contract_id bigint not null,
    additional_information varchar(255)
) engine = InnoDB;

create table property_designation
(
    contract_id bigint not null,
    property_designation   varchar(255)
) engine = InnoDB;

create table stakeholder
(
    id                  bigint not null auto_increment,
    address_type        varchar(255),
    attention           varchar(255),
    country             varchar(255),
    email_address       varchar(255),
    first_name          varchar(255),
    last_name           varchar(255),
    organization_name   varchar(255),
    organization_number varchar(255),
    party_id            varchar(255),
    phone_number        varchar(255),
    postal_code         varchar(255),
    roles               varchar(255),
    street_address      varchar(255),
    town                varchar(255),
    type                varchar(255),
    primary key (id)
) engine = InnoDB;

alter table if exists contract
    add constraint uq_contract_contract_id_version unique (contract_id, version);

alter table if exists contract_stakeholder
    add constraint uq_contract_stakeholder_stakeholder_id unique (stakeholder_id);

create index idx_contract_property_designation_contract_id
    on property_designation (contract_id);

alter table if exists contract_stakeholder
    add constraint fk_contract_stakeholder_stakeholder_id
        foreign key (stakeholder_id)
            references stakeholder (id);

alter table if exists contract_stakeholder
    add constraint fk_contract_stakeholder_contract_entity_id
        foreign key (contract_id)
            references contract (id);

alter table if exists additional_information
    add constraint fk_additional_information_contract_id
        foreign key (contract_id)
            references contract (id);

alter table if exists property_designation
    add constraint fk_contract_property_designation_contract_id
        foreign key (contract_id)
            references contract (id);
