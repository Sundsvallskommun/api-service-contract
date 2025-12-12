ALTER TABLE property_designation RENAME COLUMN property_designation TO name;
ALTER TABLE property_designation ADD COLUMN  IF NOT EXISTS district VARCHAR(255);
