create table outbox
(
    id          bigint       not null auto_increment,
    contract_id varchar(10)  not null,
    event_type  varchar(64)  not null,
    payload     json         not null,
    retries     int          not null default 0,
    created_at  datetime(6)  not null,
    last_error  varchar(512),
    primary key (id),
    index idx_outbox_retries (retries)
) engine = InnoDB;
