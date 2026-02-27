INSERT INTO contract (id, contract_id, version, status, municipality_id, description,
                      signed_by_witness, area, auto_extend, end_date, lease_type, lease_duration,
                      lease_extension, lease_duration_unit, lease_extension_unit, start_date, external_reference_id, invoice_interval, invoiced_in,
                      leasehold_description, leasehold_type, object_identity,
                      fee_currency, fee_yearly, fee_monthly, fee_total, fee_total_as_text, fee_index_type, fee_index_year, fee_index_number, fee_indexation_rate,
                      type,
                      current_period_start_date, current_period_end_date, notice_date, notice_given_by)
VALUES (1, '2024-12345', 1, 'DRAFT', '1984',
        'someOldDescription',
        false, 12, true, '2023-10-10', 'LAND_LEASE_RESIDENTIAL', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'SEK', 234.56, 123.45, 500, 'five hundred', 'KPI 80', 2021, 2, 0.5,
        'LEASE_AGREEMENT',
        null, null, null, null),

       (2, '2024-23456', 1, 'DRAFT', '1984',
        'someDescription',
        false, 12, true, '2023-10-10', 'LAND_LEASE_RESIDENTIAL', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0002',
        'QUARTERLY', 'ARREARS', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'EUR', 1000, 120, 5000, 'five thousand', 'KPI 80', 2021, 3, 0.3,
        'LEASE_AGREEMENT',
        null, null, null, null),

       (3, '2024-12345', 2, 'ACTIVE', '1984',
        'someDescription',
        true, 12, true, '2023-10-10', 'LAND_LEASE_RESIDENTIAL', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'SEK', 234.56, 123.45, 500, 'five hundred', null, 2021, 2, 0.5,
        'PURCHASE_AGREEMENT',
        '2023-10-02', '2024-10-02', '2024-06-01', 'LESSOR'),

       (4, '2024-34567', 1, 'ACTIVE', '1984',
        'Land lease residential',
        false, 500, false, '2025-12-31', 'LAND_LEASE_RESIDENTIAL', 5, 2, 'YEARS', 'YEARS', '2024-01-01', 'MK-TEST0004',
        'MONTHLY', 'ADVANCE', null, null, 'objectIdentity4',
        'SEK', 12000, 1000, 60000, 'sixty thousand', 'KPI', 2023, 1, 0.25,
        'LEASE_AGREEMENT',
        '2024-01-01', '2029-01-01', null, null),

       (5, '2024-45678', 1, 'TERMINATED', '1984',
        'Object lease agreement',
        true, 200, true, '2024-06-30', null, 3, 1, 'YEARS', 'MONTHS', '2023-07-01', 'MK-TEST0005',
        'YEARLY', 'ARREARS', null, null, 'objectIdentity5',
        'SEK', 6000, 500, 30000, 'thirty thousand', null, 2022, 3, 0.10,
        'OBJECT_LEASE',
        null, null, '2024-03-15', 'LESSEE'),

       (6, '2024-56789', 1, 'DRAFT', '1984',
        'Minimal purchase agreement',
        false, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null,
        null, null, null, null, null, null, null, null, null,
        'PURCHASE_AGREEMENT',
        null, null, null, null);

INSERT INTO fee_additional_information (contract_id, additional_information)
VALUES (1, 'someAdditionalInfo1'),
       (1, 'someAdditionalInfo2'),
       (3, 'someAdditionalInfo3'),
       (3, 'someAdditionalInfo4');

INSERT INTO term_group (id, header, term_type, contract_id)
VALUES (1, 'Basic Terms', 'INDEX', 1),
       (2, 'Additional Basic Terms', 'ADDITIONAL', 1),
       (3, 'Basic Terms', 'INDEX', 2),
       (4, 'Additional Basic Terms', 'ADDITIONAL', 2),
       (5, 'Basic Terms Here', 'INDEX', 3),
       (6, 'More information', 'ADDITIONAL', 3);

INSERT INTO term_group_term (term_group_id, term_name, description)
VALUES (1, 'Parties', 'The parties involved in the lease agreement'),
       (2, 'Parties', 'The parties involved in the additional lease agreement'),
       (3, 'Some Parties', 'Description for basic terms'),
       (4, 'Party', 'Additional terms'),
       (5, 'Donald must be happy', 'Something something'),
       (6, 'Respected by all parties', 'No pöle vaulting indoors');

INSERT INTO extra_parameter_group (id, name, contract_id)
VALUES (1, 'someParameters', 1),
       (2, 'someParameters2', 2),
       (3, 'someParameters3', 3);

INSERT INTO extra_parameter (extra_parameter_group_id, parameter_key, parameter_value)
VALUES (1, 'key1', 'value1'),
       (1, 'key2', 'value2'),
       (2, 'key3', 'value3'),
       (2, 'key4', 'value5'),
       (3, 'key5', 'value5'),
       (3, 'key6', 'value6');

INSERT INTO contract_notice (contract_id, party, period_of_notice, unit)
VALUES (1, 'LESSOR', 3, 'MONTHS'),
       (1, 'LESSEE', 2, 'MONTHS'),
       (2, 'LESSOR', 30, 'DAYS'),
       (2, 'LESSEE', 60, 'DAYS'),
       (3, 'LESSOR', 1, 'YEARS'),
       (3, 'LESSEE', 2, 'YEARS'),
       (4, 'LESSOR', 6, 'MONTHS'),
       (4, 'LESSEE', 3, 'MONTHS');

INSERT INTO property_designation (contract_id, name, district)
VALUES (1, 'SUNDSVALL NORRMALM 1:1', "District 1"),
       (2, 'SUNDSVALL NORRMALM 2:1', "District 2"),
       (3, 'SUNDSVALL NORRMALM 1:1', "District 3");

INSERT INTO additional_information (contract_id, additional_information)
VALUES (1, 'Some additional information'),
       (2, 'More additional information'),
       (3, 'Even more additional information');

INSERT INTO attachment (id, contract_id, municipality_id, category, filename, mime_type, note, content, created)
VALUES (1, '2024-12345', '1984', 'CONTRACT', 'someFile.pdf', 'application/pdf', 'someNote', 'someBase64Content', '2024-01-15 10:30:00');

INSERT INTO stakeholder (id, address_type, attention, country, email_address, first_name,
                         last_name, organization_name, organization_number, party_id, phone_number,
                         postal_code, street_address, town, type, roles)
VALUES (1, 'POSTAL_ADDRESS', 'someAttention', 'SE', 'someEmail', 'someFirstName', 'someLastName',
        'someOrganizationName', '771122-1234', '40f14de6-815d-44b2-a34d-b1d38b628e07',
        'somePhoneNumber',
        '12345', 'someStreetAddress',
        'someTown', 'PERSON', 'SIGNATORY'),
       (2, 'VISITING_ADDRESS', 'someAttention', 'SE', 'someEmail', 'someFirstName', 'someLastName',
        'someOrganizationName', '771122-1234', '40f14de9-815d-44a5-a34d-b1d38b628e07',
        'somePhoneNumber',
        '12345', 'someStreetAddress',
        'someTown', 'PERSON', 'POWER_OF_ATTORNEY_ROLE,SIGNATORY'),
       (3, 'POSTAL_ADDRESS', 'someAttention', 'SE', 'someEmail', 'someFirstName', 'someLastName',
        'someOrganizationName', '771122-1234', '40f14de6-815d-44b2-a34d-b1d38b628e07',
        'somePhoneNumber',
        '12345', 'someStreetAddress',
        'someTown', 'PERSON', 'SIGNATORY');

INSERT INTO contract_stakeholder (contract_id, stakeholder_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

INSERT INTO stakeholder_parameter(id, stakeholder_id, display_name, parameters_key)
VALUES (201, 2, 'Parameter X', 'parameterX');

INSERT INTO stakeholder_parameter_values(stakeholder_parameter_id, `value`)
VALUES (201, 'value-x1'),
       (201, 'value-x2');
