-- Remove contract versioning: collapse every contract to its latest version (one row per contract_id),
-- then drop the `version` column and its unique constraint, replacing the logical key with
-- (municipality_id, contract_id). `lock_version` (JPA optimistic lock) and `contract_id_counter`
-- (id generation) are unrelated and kept.
--
-- Child tables FK to contract(id) (the numeric PK, distinct per version) with no ON DELETE CASCADE,
-- so the orphaned children of non-latest versions are deleted explicitly, deepest first. A row is a
-- "non-latest version" when its version is below MAX(version) for its contract_id.

-- Guard against stray NULL versions. The application never writes them (version starts at 1), but a
-- manual data fix could leave one. Normalize to 0 so such a row collapses like any other instead of
-- surviving the comparisons below (NULL < MAX is NULL, i.e. "not deleted") and then breaking the new
-- (municipality_id, contract_id) unique constraint added at the end.
UPDATE contract SET version = 0 WHERE version IS NULL;

-- --- term groups (term_group_term -> term_group -> contract) ---
DELETE tgt FROM term_group_term tgt
	JOIN term_group tg ON tgt.term_group_id = tg.id
	JOIN contract c ON tg.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE tg FROM term_group tg
	JOIN contract c ON tg.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

-- --- extra parameters (extra_parameter -> extra_parameter_group -> contract) ---
DELETE ep FROM extra_parameter ep
	JOIN extra_parameter_group epg ON ep.extra_parameter_group_id = epg.id
	JOIN contract c ON epg.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE epg FROM extra_parameter_group epg
	JOIN contract c ON epg.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

-- --- element-collection child tables (each FK contract_id -> contract.id) ---
DELETE an FROM additional_information an
	JOIN contract c ON an.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE cn FROM contract_notice cn
	JOIN contract c ON cn.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE pd FROM property_designation pd
	JOIN contract c ON pd.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE fai FROM fee_additional_information fai
	JOIN contract c ON fai.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

-- --- stakeholders of non-latest versions (owned per contract: contract_stakeholder.stakeholder_id is unique,
--     so each stakeholder belongs to exactly one contract version). Capture the ids first, then delete their
--     parameter values/params, the junction rows, and finally the stakeholder rows. ---
CREATE TEMPORARY TABLE tmp_orphan_stakeholders AS
	SELECT cs.stakeholder_id AS stakeholder_id
	FROM contract_stakeholder cs
	JOIN contract c ON cs.contract_id = c.id
	WHERE c.version < (SELECT MAX(c2.version) FROM contract c2 WHERE c2.contract_id = c.contract_id);

DELETE FROM stakeholder_parameter_values
	WHERE stakeholder_parameter_id IN (
		SELECT id FROM stakeholder_parameter
		WHERE stakeholder_id IN (SELECT stakeholder_id FROM tmp_orphan_stakeholders));

DELETE FROM stakeholder_parameter
	WHERE stakeholder_id IN (SELECT stakeholder_id FROM tmp_orphan_stakeholders);

DELETE FROM contract_stakeholder
	WHERE stakeholder_id IN (SELECT stakeholder_id FROM tmp_orphan_stakeholders);

DELETE FROM stakeholder
	WHERE id IN (SELECT stakeholder_id FROM tmp_orphan_stakeholders);

DROP TEMPORARY TABLE tmp_orphan_stakeholders;

-- --- finally, delete the non-latest contract rows (derived table wrapper required by MariaDB to
--     self-reference the table being deleted) ---
DELETE FROM contract WHERE id IN (
	SELECT id FROM (
		SELECT c.id AS id
		FROM contract c
		JOIN (SELECT contract_id, MAX(version) AS mx FROM contract GROUP BY contract_id) m
			ON c.contract_id = m.contract_id
		WHERE c.version < m.mx) doomed);

-- --- drop the version column + its unique constraint, add the new logical key ---
ALTER TABLE contract DROP CONSTRAINT uq_contract_contract_id_version;
ALTER TABLE contract DROP COLUMN version;
ALTER TABLE contract ADD CONSTRAINT uq_contract_municipality_id_contract_id UNIQUE (municipality_id, contract_id);
