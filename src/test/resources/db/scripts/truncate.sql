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
truncate table fee_additional_information;
truncate table term_group_term;
truncate table term_group;
truncate table extra_parameter;
truncate table extra_parameter_group;

set foreign_key_checks = 1;
