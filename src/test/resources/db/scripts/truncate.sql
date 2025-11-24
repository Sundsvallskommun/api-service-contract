set foreign_key_checks = 0;

truncate table additional_information;
truncate table attachment;
truncate table contract;
truncate table contract_stakeholder;
truncate table contract_notice;
truncate table property_designation;
truncate table stakeholder;
truncate table stakeholder_parameter;
truncate table stakeholder_parameter_values;

set foreign_key_checks = 1;
