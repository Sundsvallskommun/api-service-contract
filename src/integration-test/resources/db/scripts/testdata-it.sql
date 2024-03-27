INSERT INTO contract (id, contract_id, version, status, municipality_id, case_id, index_terms, description, additional_terms, signed_by_witness)
VALUES (1, '2024-12345', 1, 'DRAFT', '1984', 1, '[]', 'someDescription', '[]', 1),
       (2, '2024-23456', 1, 'DRAFT', '1984', 1, '[]', 'someDescription', '[]', 0);

INSERT INTO land_lease_contract (id, area, auto_extend, end, land_lease_type, lease_duration,
                                 lease_extension, period_of_notice, start, external_reference_id,
                                 invoice_interval, invoiced_in, leasehold_description,
                                 leasehold_type, object_identity, usufruct_type, lease_fees)
VALUES (1, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 2, '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'HUNTING', '{"currency":"SEK","yearly":234.56,"monthly":123.45,"total":500,"totalAsText":"five hundred","indexNumber":2,"indexYear":2021,"additionalInformation":["someAdditionalInfo1","someAdditionalInfo2"]}'),
       (2, 12, true, '2023-10-10', 1, 2, 1, 2, '2023-10-02', 'MK-TEST0002',
        'QUARTERLY', 'ARREARS', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity',
        'HUNTING', '{"currency":"EUR","yearly":1000,"monthly":120,"total":5000,"totalAsText":"five thousand","indexNumber":3,"indexYear":2021,"additionalInformation":[]}');

INSERT INTO land_lease_contract_property_designations (contract_id, property_designations)
VALUES (1, 'SUNDSVALL NORRMALM 1:1'),
       (2, 'SUNDSVALL NORRMALM 2:1');

INSERT INTO land_lease_contract_leasehold_additional_information (land_lease_contract_id, additional_information)
VALUES (1, 'Some additional information'),
       (2, 'More additional information');

INSERT INTO attachment (id, category, extension, file, mime_type, name, note)
    VALUE (1, 'CONTRACT', '.pdf', 'someFile', 'application/pdf', 'someName', 1);

INSERT INTO contract_attachments (contract_entity_id, attachments_id)
VALUES (1, 1);


INSERT INTO stakeholder (id, address_type, attention, country, email_address, first_name,
                         last_name, organization_name, organization_number, person_id, phone_number,
                         postal_code, street_address, town, type)
VALUES (1, 'POSTAL_ADDRESS', 'someAttention', 'SE', 'someEmail', 'someFirstName', 'someLastName',
        'someOrganizationName', '771122-1234', '40f14de6-815d-44b2-a34d-b1d38b628e07',
        'somePhoneNumber',
        '12345', 'someStreetAddress',
        'someTown', 'PERSON'),
       (2, 'VISITING_ADDRESS', 'someAttention', 'SE', 'someEmail', 'someFirstName', 'someLastName',
        'someOrganizationName', '771122-1234', '40f14de9-815d-44a5-a34d-b1d38b628e07',
        'somePhoneNumber',
        '12345', 'someStreetAddress',
        'someTown', 'PERSON');

INSERT INTO contract_stakeholders (contract_entity_id, stakeholders_id)
VALUES (1, 1),
       (2, 2);

INSERT INTO stakeholder_roles (stakeholder_entity_id, role)
VALUES (1, 'SIGNATORY'),
       (2, 'POWER_OF_ATTORNEY_ROLE');
