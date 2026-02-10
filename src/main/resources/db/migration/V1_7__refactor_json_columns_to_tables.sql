-- Add fee columns to contract table (embedded fees)
alter table contract add column fee_currency varchar(255);
alter table contract add column fee_yearly decimal(38,2);
alter table contract add column fee_monthly decimal(38,2);
alter table contract add column fee_total decimal(38,2);
alter table contract add column fee_total_as_text varchar(255);
alter table contract add column fee_index_type varchar(255);
alter table contract add column fee_index_year integer;
alter table contract add column fee_index_number integer;
alter table contract add column fee_indexation_rate decimal(38,2);

-- Create fee_additional_information collection table
create table fee_additional_information (
    contract_id bigint not null,
    additional_information varchar(255),
    constraint fk_fee_additional_information_contract_id foreign key (contract_id) references contract(id)
);

-- Create term_group entity table
create table term_group (
    id bigint not null auto_increment,
    header varchar(255),
    term_type varchar(32),
    contract_id bigint,
    primary key (id),
    constraint fk_term_group_contract_id foreign key (contract_id) references contract(id)
);

-- Create term_group_term collection table
create table term_group_term (
    term_group_id bigint not null,
    term_name varchar(255),
    description varchar(1024),
    constraint fk_term_group_term_term_group_id foreign key (term_group_id) references term_group(id)
);

-- Create extra_parameter_group entity table
create table extra_parameter_group (
    id bigint not null auto_increment,
    name varchar(255),
    contract_id bigint,
    primary key (id),
    constraint fk_extra_parameter_group_contract_id foreign key (contract_id) references contract(id)
);

-- Create extra_parameter collection table
create table extra_parameter (
    extra_parameter_group_id bigint not null,
    parameter_key varchar(255) not null,
    parameter_value varchar(255),
    primary key (extra_parameter_group_id, parameter_key),
    constraint fk_extra_parameter_extra_parameter_group_id foreign key (extra_parameter_group_id) references extra_parameter_group(id)
);

-- Drop old JSON columns
alter table contract drop column fees;
alter table contract drop column index_terms;
alter table contract drop column additional_terms;
alter table contract drop column extra_parameters;
