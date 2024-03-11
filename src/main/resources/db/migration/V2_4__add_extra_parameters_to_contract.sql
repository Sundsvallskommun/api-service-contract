create table if not exists contract_extra_parameter(
    contract_id varchar(10) not null,
    parameter_key varchar(255) not null,
    parameter_value varchar(255) not null,
    primary key (contract_id, parameter_key)) engine=InnoDB;

create index if not exists idx_extra_parameter_contract_id
    on contract_extra_parameter(contract_id);

alter table if exists contract_extra_parameter
    add constraint fk_extra_parameter_contract_id
        foreign key (contract_id)
            references contract (id);