INSERT INTO contract (id, version, status, case_id, index_terms, description, additional_terms, signed_by_witness)
VALUES (1, 1, 'ACTIVE', 1, 'someIndexTerms', 'someDescription', 'someAdditionalTerms', 1),
       (2, 1, 'ACTIVE', 1, 'someIndexTerms', 'someDescription', 'someAdditionalTerms', 0);

INSERT INTO land_lease_contract (area, auto_extend, end, land_lease_type,
                                 lease_duration, lease_extension, period_of_notice,
                                 rental, start, id,
                                 external_reference_id,
                                 invoice_interval, leasehold_description,
                                 leasehold_type, object_identity,
                                 property_designation, usufruct_type)
VALUES (12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 2, 123.00, '2023-10-02', 1, 'MK-TEST0001',
        'QUARTERLY', 'SomeLeaseholdDescription',
        'AGRICULTURE', 'someObjectIdentity', 'SUNDSVALL GRANLO 2:1', 'HUNTING'),

       (12, true, '2023-10-10', 1, 2, 1, 2, 123.00, '2023-10-02', 2, 'MK-TEST0002',
        'QUARTERLY', 'SomeLeaseholdDescription',
        'AGRICULTURE', 'someObjectIdentity', 'SUNDSVALL GRANLO 2:1', 'HUNTING');

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
       (1, 2);

INSERT INTO stakeholder_roles (stakeholder_entity_id, role)
VALUES (1, 'SIGNATORY'),
       (2, 'POWER_OF_ATTORNEY_ROLE');
