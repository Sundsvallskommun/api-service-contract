alter table attachment
    change category
        category enum('CONTRACT','OTHER');

alter table contract
    change status
        status enum('ACTIVE','DRAFT','TERMINATED');

alter table stakeholder_roles
    change role
        role enum ('BUYER','CONTACT_PERSON','GRANTOR','LAND_OWNER','LEASE_HOLDER','POWER_OF_ATTORNEY_CHECK','POWER_OF_ATTORNEY_ROLE','SELLER','SIGNATORY')
