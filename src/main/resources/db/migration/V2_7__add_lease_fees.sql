alter table land_lease_contract add column lease_fees json after rental;
alter table land_lease_contract drop column rental;
