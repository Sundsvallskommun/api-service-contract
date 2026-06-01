-- ============================================================================================================
-- Billing data violation diagnostics
-- ============================================================================================================
-- These read-only SELECTs identify EXISTING contracts whose data violates the billing constraints that are now
-- enforced at create/update/patch time (see ContractValidator and Fees.additionalInformation @NotBlank/@Size).
-- They are NOT Flyway migrations (this folder is outside classpath:db/migration, so Flyway never runs them) and
-- must NOT silently mutate data — the listed rows require domain knowledge to correct.
--
-- Run each query against TEST and PRODUCTION, then have the responsible team correct the listed contracts
-- (e.g. add the missing PRIMARY_BILLING_PARTY stakeholder, complete or clear the fee index trio, add the missing
-- property designation, complete recipient name/address, shorten over-long descriptions). Correcting these stops
-- the BillingPreprocessor 400s / open circuit breakers.
--
-- Every query is restricted to the LATEST version of each contract, since that is what the billing pipeline
-- fetches. Adjust the WHERE clause if you also want to inspect historical versions.
-- ============================================================================================================
-- ------------------------------------------------------------------------------------------------------------
-- 1. Invoice-bound contracts missing a PRIMARY_BILLING_PARTY stakeholder
--    (both invoice_interval and invoiced_in set, but no stakeholder carries the PRIMARY_BILLING_PARTY role)
-- ------------------------------------------------------------------------------------------------------------
SELECT
    c.municipality_id,
    c.contract_id,
    c.version
FROM
    contract c
JOIN(
        SELECT
            municipality_id,
            contract_id,
            MAX( version ) AS version
        FROM
            contract
        GROUP BY
            municipality_id,
            contract_id
    ) latest ON
    latest.municipality_id = c.municipality_id
    AND latest.contract_id = c.contract_id
    AND latest.version = c.version
WHERE
    c.invoice_interval IS NOT NULL
    AND c.invoiced_in IS NOT NULL
    AND NOT EXISTS(
        SELECT
            1
        FROM
            contract_stakeholder cs
        JOIN stakeholder s ON
            s.id = cs.stakeholder_id
        WHERE
            cs.contract_id = c.id
            AND s.roles LIKE '%PRIMARY_BILLING_PARTY%'
    );

-- ------------------------------------------------------------------------------------------------------------
-- 2. Contracts with partial fee index data
--    (any of fee_index_type / fee_index_year / fee_index_number set, but not all three with index_number > 0)
-- ------------------------------------------------------------------------------------------------------------
SELECT
    c.municipality_id,
    c.contract_id,
    c.version,
    c.fee_index_type,
    c.fee_index_year,
    c.fee_index_number
FROM
    contract c
JOIN(
        SELECT
            municipality_id,
            contract_id,
            MAX( version ) AS version
        FROM
            contract
        GROUP BY
            municipality_id,
            contract_id
    ) latest ON
    latest.municipality_id = c.municipality_id
    AND latest.contract_id = c.contract_id
    AND latest.version = c.version
WHERE
    (
        (
            c.fee_index_type IS NOT NULL
            AND TRIM( c.fee_index_type )<> ''
        )
        OR c.fee_index_year IS NOT NULL
        OR c.fee_index_number IS NOT NULL
    )
    AND NOT(
        c.fee_index_type IS NOT NULL
        AND TRIM( c.fee_index_type )<> ''
        AND c.fee_index_year IS NOT NULL
        AND c.fee_index_number IS NOT NULL
        AND c.fee_index_number > 0
    );

-- ------------------------------------------------------------------------------------------------------------
-- 3. Land/site lease contracts missing a property designation with a name
-- ------------------------------------------------------------------------------------------------------------
SELECT
    c.municipality_id,
    c.contract_id,
    c.version,
    c.lease_type
FROM
    contract c
JOIN(
        SELECT
            municipality_id,
            contract_id,
            MAX( version ) AS version
        FROM
            contract
        GROUP BY
            municipality_id,
            contract_id
    ) latest ON
    latest.municipality_id = c.municipality_id
    AND latest.contract_id = c.contract_id
    AND latest.version = c.version
WHERE
    (
        c.lease_type LIKE 'LAND_LEASE_%'
        OR c.lease_type LIKE 'SITE_LEASE_%'
    )
    AND NOT EXISTS(
        SELECT
            1
        FROM
            property_designation pd
        WHERE
            pd.contract_id = c.id
            AND pd.name IS NOT NULL
            AND TRIM( pd.name )<> ''
    );

-- ------------------------------------------------------------------------------------------------------------
-- 4. Fee additional-information entries longer than 30 characters (invalid invoice row descriptions)
-- ------------------------------------------------------------------------------------------------------------
SELECT
    c.municipality_id,
    c.contract_id,
    c.version,
    fai.additional_information,
    CHAR_LENGTH( fai.additional_information ) AS LENGTH
FROM
    contract c
JOIN(
        SELECT
            municipality_id,
            contract_id,
            MAX( version ) AS version
        FROM
            contract
        GROUP BY
            municipality_id,
            contract_id
    ) latest ON
    latest.municipality_id = c.municipality_id
    AND latest.contract_id = c.contract_id
    AND latest.version = c.version
JOIN fee_additional_information fai ON
    fai.contract_id = c.id
WHERE
    CHAR_LENGTH( fai.additional_information )> 30;

-- ------------------------------------------------------------------------------------------------------------
-- 5. Invoice-bound contracts whose PRIMARY_BILLING_PARTY recipient lacks a usable name
--    (neither organization_name nor a first_name + last_name pair) or a complete postal address
--    (street address, postal code and town). These map to BillingPreprocessor's recipient/address constraints.
-- ------------------------------------------------------------------------------------------------------------
SELECT
    c.municipality_id,
    c.contract_id,
    c.version,
    s.organization_name,
    s.first_name,
    s.last_name,
    s.street_address,
    s.postal_code,
    s.town
FROM
    contract c
JOIN(
        SELECT
            municipality_id,
            contract_id,
            MAX( version ) AS version
        FROM
            contract
        GROUP BY
            municipality_id,
            contract_id
    ) latest ON
    latest.municipality_id = c.municipality_id
    AND latest.contract_id = c.contract_id
    AND latest.version = c.version
JOIN contract_stakeholder cs ON
    cs.contract_id = c.id
JOIN stakeholder s ON
    s.id = cs.stakeholder_id
WHERE
    c.invoice_interval IS NOT NULL
    AND c.invoiced_in IS NOT NULL
    AND s.roles LIKE '%PRIMARY_BILLING_PARTY%'
    AND(
        (
            (
                s.organization_name IS NULL
                OR TRIM( s.organization_name )= ''
            )
            AND(
                s.first_name IS NULL
                OR TRIM( s.first_name )= ''
                OR s.last_name IS NULL
                OR TRIM( s.last_name )= ''
            )
        )
        OR s.street_address IS NULL
        OR TRIM( s.street_address )= ''
        OR s.postal_code IS NULL
        OR TRIM( s.postal_code )= ''
        OR s.town IS NULL
        OR TRIM( s.town )= ''
    );
