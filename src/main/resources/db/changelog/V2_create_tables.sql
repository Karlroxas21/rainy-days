
ALTER TABLE entries
ALTER COLUMN entry_type TYPE varchar USING entry_type::text;