INSERT INTO contract (id, contract_id, version, status, municipality_id, index_terms, description, additional_terms,
                      signed_by_witness, area, auto_extend, end, lease_type, lease_duration,
                      lease_extension, lease_duration_unit, lease_extension_unit, start, external_reference_id, invoice_interval, invoiced_in,
                      leasehold_description, leasehold_type, object_identity, fees, extra_parameters,
                      type)
VALUES (1, '2024-12345', 1, 'DRAFT', '1984',
        '[{"header": "Basic Terms","terms": [{"description": "The parties involved in the lease agreement", "term": "Parties"}]}]',
        'someOldDescription',
        '[{"header": "Additional Basic Terms","terms": [{"description": "The parties involved in the additional lease agreement", "term": "Parties"}]}]',
        false, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity', 
        '{"currency":"SEK","yearly":234.56,"monthly":123.45,"total":500,"totalAsText":"five hundred","indexNumber":2,"indexationRate": 0.5,"indexYear":2021,"additionalInformation":["someAdditionalInfo1","someAdditionalInfo2"]}',
        '[{"name":"someParameters","parameters":{"key1":"value1","key2":"value2"}}]', 'LEASE_AGREEMENT'),

       (2, '2024-23456', 1, 'DRAFT', '1984',
        '[{"header": "Basic Terms","terms": [{"description": "Description for basic terms", "term": "Some Parties"}]}]',
        'someDescription',
        '[{"header": "Additional Basic Terms","terms": [{"description": "Additional terms", "term": "Party"}]}]',
        false, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0002',
        'QUARTERLY', 'ARREARS', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity', 
        '{"currency":"EUR","yearly":1000,"monthly":120,"total":5000,"totalAsText":"five thousand","indexNumber":3,"indexationRate": 0.3,"indexYear":2021,"additionalInformation":[]}',
        '[{"name":"someParameters2","parameters":{"key3":"value3","key4":"value5"}}]', 'LEASE_AGREEMENT'),

       (3, '2024-12345', 2, 'ACTIVE', '1984',
        '[{"header": "Basic Terms Here","terms": [{"description": "Something something", "term": "Donald must be happy"}]}]',
        'someDescription',
        '[{"header": "More information","terms": [{"description": "No pöle vaulting indoors", "term": "Respected by all parties"}]}]',
        true, 12, true, '2023-10-10', 'LEASEHOLD', 2, 1, 'YEARS', 'YEARS', '2023-10-02', 'MK-TEST0001',
        'QUARTERLY', 'ADVANCE', 'SomeLeaseholdDescription', 'AGRICULTURE', 'someObjectIdentity', 
        '{"currency":"SEK","yearly":234.56,"monthly":123.45,"total":500,"totalAsText":"five hundred","indexNumber":2,"indexationRate": 0.5,"indexYear":2021,"additionalInformation":["someAdditionalInfo3","someAdditionalInfo4"]}',
        '[{"name":"someParameters3","parameters":{"key5":"value5","key6":"value6"}}]', 'PURCHASE_AGREEMENT');

INSERT INTO contract_notice (contract_id, party, period_of_notice, unit) 
VALUES (1, 'LESSOR', 3, 'MONTHS'),
       (1, 'LESSEE', 2, 'MONTHS'),
       (2, 'LESSOR', 30, 'DAYS'),
       (2, 'LESSEE', 60, 'DAYS'),
       (3, 'LESSOR', 1, 'YEARS'),
       (3, 'LESSEE', 2, 'YEARS');
    
INSERT INTO property_designation (contract_id, name, district)
VALUES (1, 'SUNDSVALL NORRMALM 1:1', "District 1"),
       (2, 'SUNDSVALL NORRMALM 2:1', "District 2"),
       (3, 'SUNDSVALL NORRMALM 1:1', "District 3");

INSERT INTO additional_information (contract_id, additional_information)
VALUES (1, 'Some additional information'),
       (2, 'More additional information'),
       (3, 'Even more additional information');

INSERT INTO attachment (id, contract_id, municipality_id, category, filename, mime_type, note, content)
VALUES (1, '2024-12345', '1984', 'CONTRACT', 'someFile.pdf', 'application/pdf', 'someNote', 'someBase64Content');

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
