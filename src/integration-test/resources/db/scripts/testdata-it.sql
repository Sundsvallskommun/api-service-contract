INSERT INTO contract (id, contract_id, version, status, municipality_id, index_terms, description, additional_terms, signed_by_witness)
VALUES (1, '2024-12345', 1, 'DRAFT', '1984', '[]', 'someOldDescription', '[]', 0),
       (2, '2024-23456', 1, 'DRAFT', '1984', '[]', 'someDescription', '[]', 0),
       (3, '2024-12345', 2, 'ACTIVE', '1984', '[]', 'someDescription', '[]', 1);

INSERT INTO land_lease_contract (id, area, auto_extend, end, land_lease_type, lease_duration,
                                 lease_extension, period_of_notice, start, external_reference_id,
                                 invoice_interval, invoiced_in, leasehold_description,
                                 leasehold_type, object_identity, usufruct_type, lease_fees)
VALUES (1, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 2, '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'HUNTING', '{"currency":"SEK","yearly":234.56,"monthly":123.45,"total":500,"totalAsText":"five hundred","indexNumber":2,"indexYear":2021,"additionalInformation":["someAdditionalInfo1","someAdditionalInfo2"]}'),
       (2, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 2, '2023-10-02', 'MK-TEST0002',
        'QUARTERLY', 'ARREARS', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'HUNTING', '{"currency":"EUR","yearly":1000,"monthly":120,"total":5000,"totalAsText":"five thousand","indexNumber":3,"indexYear":2021,"additionalInformation":[]}'),
       (3, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 2, '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'HUNTING', '{"currency":"SEK","yearly":234.56,"monthly":123.45,"total":500,"totalAsText":"five hundred","indexNumber":2,"indexYear":2021,"additionalInformation":["someAdditionalInfo1","someAdditionalInfo2"]}');

INSERT INTO land_lease_contract_property_designation (land_lease_contract_id, property_designation)
VALUES (1, 'SUNDSVALL NORRMALM 1:1'),
       (2, 'SUNDSVALL NORRMALM 2:1'),
       (3, 'SUNDSVALL NORRMALM 1:1');

INSERT INTO land_lease_contract_leasehold_additional_information (land_lease_contract_id, additional_information)
VALUES (1, 'Some additional information'),
       (2, 'More additional information'),
       (3, 'Some additional information');

INSERT INTO attachment (id, contract_id, municipality_id, category, filename, mime_type, note, content)
    VALUE (1, '2024-12345', '1984', 'CONTRACT', 'someFile.pdf', 'application/pdf', 'someNote', 'someBase64Content');

INSERT INTO stakeholder (id, address_type, attention, country, email_address, first_name,
                         last_name, organization_name, organization_number, person_id, phone_number,
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
