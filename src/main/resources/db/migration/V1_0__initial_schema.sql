create sequence if not exists contract_id_seq start with 1 increment by 1;

create table attachment (
    id                      bigint not null auto_increment,
    contract_id             varchar(10) not null,
    municipality_id         varchar(4),
    category                enum ('CONTRACT','OTHER'),
    filename                varchar(255),
    mime_type               varchar(255),
    note                    varchar(255),
    content                 longblob,
    primary key (id)
) engine=InnoDB;

create table contract (
    id                      bigint not null auto_increment,
    municipality_id         varchar(4),
    contract_id             varchar(10),
    version                 integer,
    type                    enum ('LAND_LEASE'),
    status                  enum ('ACTIVE','DRAFT','TERMINATED'),
    case_id                 bigint,
    description             varchar(4096),
    signed_by_witness       bit,
    additional_terms        json,
    index_terms             json,
    extra_parameters        json,
    primary key (id)
) engine=InnoDB;

create table land_lease_contract (
    id                      bigint not null,
    land_lease_type         enum ('LEASEHOLD','USUFRUCT','SITELEASEHOLD'),
    leasehold_type          enum ('AGRICULTURE','APARTMENT','BUILDING','DWELLING','OTHER'),
    usufruct_type           enum ('HUNTING','FISHING','MAINTENANCE','OTHER'),
    leasehold_description   varchar(255),
    object_identity         varchar(255),
    area                    integer,
    auto_extend             bit,
    start                   date,
    end                     date,
    lease_duration          integer,
    lease_extension         integer,
    period_of_notice        integer,
    lease_fees              varchar(255),
    invoice_interval        enum ('YEARLY','QUARTERLY','MONTHLY'),
    invoiced_in             enum ('ADVANCE','ARREARS'),
    area_data               longblob,
    external_reference_id   varchar(255),
    primary key (id)
) engine=InnoDB;

create table land_lease_contract_leasehold_additional_information (
    land_lease_contract_id bigint not null,
    additional_information varchar(255)
) engine=InnoDB;

create table contract_stakeholder (
    contract_id             bigint not null,
    stakeholder_id          bigint not null
) engine=InnoDB;

create table land_lease_contract_property_designation (
    land_lease_contract_id  bigint not null,
    property_designation    varchar(255)
) engine=InnoDB;

create table stakeholder (
    id                      bigint not null auto_increment,
    type                    enum ('PERSON','COMPANY','ASSOCIATION'),
    organization_name       varchar(255),
    organization_number     varchar(255),
    person_id               varchar(255),
    first_name              varchar(255),
    last_name               varchar(255),
    address_type            enum ('POSTAL_ADDRESS','BILLING_ADDRESS','VISITING_ADDRESS'),
    attention               varchar(255),
    street_address          varchar(255),
    postal_code             varchar(255),
    town                    varchar(255),
    country                 varchar(255),
    email_address           varchar(255),
    phone_number            varchar(255),
    primary key (id)
) engine=InnoDB;

create table stakeholder_role (
    stakeholder_id          bigint not null,
    role                    enum ('BUYER','CONTACT_PERSON','GRANTOR','LAND_OWNER','LEASE_HOLDER','POWER_OF_ATTORNEY_CHECK','POWER_OF_ATTORNEY_ROLE','SELLER','SIGNATORY')
) engine=InnoDB;

alter table if exists contract
    add constraint uq_contract_contract_id_version unique (contract_id, version);

alter table if exists contract_stakeholder
    add constraint uq_contract_stakeholder_stakeholder_id unique (stakeholder_id);

create index idx_land_lease_contract_property_designation_contract_id
    on land_lease_contract_property_designation (land_lease_contract_id);

alter table if exists attachment
    add constraint fk_attachment_contract_id
        foreign key (contract_id)
            references contract (contract_id);

alter table if exists contract_stakeholder
    add constraint fk_contract_stakeholder_stakeholder_id
        foreign key (stakeholder_id)
            references stakeholder (id);

alter table if exists contract_stakeholder
    add constraint fk_contract_stakeholder_contract_entity_id
        foreign key (contract_id)
            references contract (id);

alter table if exists land_lease_contract
    add constraint fk_land_lease_contract_contract_id
        foreign key (id)
            references contract (id);

alter table if exists land_lease_contract_leasehold_additional_information
    add constraint fk_llc_leasehold_additional_information_land_lease_contract_id
        foreign key (land_lease_contract_id)
            references land_lease_contract (id);

alter table if exists land_lease_contract_property_designation
    add constraint fk_land_lease_contract_property_designation_contract_id
        foreign key (land_lease_contract_id)
            references land_lease_contract (id);

alter table if exists stakeholder_role
    add constraint fk_stakeholder_role_stakeholder_id
        foreign key (stakeholder_id)
            references stakeholder (id);
