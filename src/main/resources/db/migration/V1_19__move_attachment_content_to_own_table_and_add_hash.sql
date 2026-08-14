-- Restructure how attachment content is stored.
--
-- 1. The binary content moves out into its own table. Keeping the blob in the attachment row meant every metadata read
--    - listing a contract's attachments, or reading a contract, which embeds the same metadata - dragged the file bytes
--    along. Behind a lazily fetched association it is loaded only by the endpoint that streams the file.
--
-- 2. A content hash is added. It lets a client verify an uploaded file round-tripped intact, and lets other services
--    recognize the same file without transferring it. The hash is the lower-case hex encoded SHA-256 of the raw binary
--    content, produced by se.sundsvall.dept44.util.HashUtils and byte-for-byte identical to what
--    lower(sha2(file, 256)) over attachment_data yields, so application-side and database-side hashes are
--    interchangeable.
--
-- This migration is pure DDL - nothing is copied, backfilled or converted. There are no attachments in any environment,
-- so there is no content to move and no hash to derive; the old content column is dropped rather than migrated. Both
-- values are derived on insert going forward, and since the content is immutable once uploaded (replacing it means
-- deleting and recreating the attachment) a stored hash never goes stale.
--
-- attachment_data_id is NOT NULL, which keeps "attachment without content" out of the model rather than leaving every
-- reader to guard against it. That is only sound because the table is empty: were any row to exist, the added column
-- would take the implicit default and the foreign key below would then fail to build, so a wrong assumption about
-- existing data surfaces as a failed migration rather than as silently corrupt rows.
--
-- IF NOT EXISTS / IF EXISTS make the statements re-runnable. All of them are MariaDB syntax, including the placement of
-- IF NOT EXISTS after FOREIGN KEY.

create table if not exists attachment_data
(
    id   bigint   not null auto_increment,
    file longblob not null,
    primary key (id)
) engine = InnoDB;

alter table attachment
    add column if not exists attachment_data_id bigint not null;

-- One data row per attachment; this mirrors the uniqueness the @OneToOne mapping implies.
alter table attachment
    add unique index if not exists uk_attachment_attachment_data_id (attachment_data_id);

alter table attachment
    add constraint fk_attachment_data_attachment
        foreign key if not exists (attachment_data_id) references attachment_data (id);

alter table attachment
    drop column if exists content;

alter table attachment
    add column if not exists hash varchar(64);
