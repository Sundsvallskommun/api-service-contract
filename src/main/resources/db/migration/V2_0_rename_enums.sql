alter table attachment
    change category
        category enum('CONTRACT','OTHER');

alter table contract
    change status
        status enum('ACTIVE','DRAFT','TERMINATED');

alter table stakeholder_roles
    change role
        role tinyint check (role between 0 and 8);