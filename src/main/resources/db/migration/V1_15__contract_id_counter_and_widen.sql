-- Widen contract_id columns from varchar(10) to varchar(11) so that the new
-- per-year counter can emit 6-digit suffixes (YYYY-NNNNNN). 5-digit IDs from
-- existing rows still fit unchanged.
alter table contract   modify column contract_id varchar(11) not null;
alter table attachment modify column contract_id varchar(11) not null;
alter table outbox     modify column contract_id varchar(11) not null;

-- Per-year counter that replaces the global `contract_id_seq` sequence for
-- contract id generation. `ContractIdGenerator` upserts the (year, last_value)
-- row atomically on each contract creation, so the year-boundary reset is
-- folded into the value allocation itself — a separate midnight reset job
-- would leave a race window between the new year starting and the job firing.
--
-- `last_value` stores the most recently allocated suffix (or, after seeding,
-- the highest already-observed suffix). The next allocation is `last_value + 1`.
create table contract_id_counter
(
    `year`     int    not null,
    last_value bigint not null,
    primary key (`year`)
) engine = InnoDB;

-- Seed `last_value` from existing data so per-year numbering continues from
-- where the old sequence left off. Only canonical YYYY-NNNNN ids participate;
-- user-supplied custom contract ids are excluded by the regex filter so they
-- can't pollute the per-year max.
insert into contract_id_counter (`year`, last_value)
select cast(left(contract_id, 4) as unsigned)                          as `year`,
       max(cast(substring_index(contract_id, '-', -1) as unsigned))    as last_value
from contract
where contract_id regexp '^[0-9]{4}-[0-9]+$'
group by cast(left(contract_id, 4) as unsigned);

-- The previous global sequence is replaced by the per-year counter and is no
-- longer referenced by `ContractIdGenerator`. Drop it so it can't accidentally
-- be reintroduced.
drop sequence if exists contract_id_seq;
