CREATE OR REPLACE FUNCTION utils.make_fkeys_deferrable(p_schema_name TEXT, p_table_schema text) RETURNS void AS $$
declare
   META_INFO RECORD;
   statement TEXT;
begin
FOR META_INFO IN 
	SELECT
		tc.constraint_name, tc.table_schema, tc.table_name, kcu.column_name, 
		ccu.table_name AS foreign_table_name,
		ccu.column_name AS foreign_column_name 
	FROM 
		information_schema.table_constraints AS tc 
	JOIN 	information_schema.key_column_usage AS kcu
		ON tc.constraint_name = kcu.constraint_name
	JOIN information_schema.constraint_column_usage AS ccu
		ON ccu.constraint_name = tc.constraint_name
	WHERE 	constraint_type = 'FOREIGN KEY' AND tc.table_name=p_table_schema AND tc.table_schema=p_schema_name
LOOP
   statement := 'ALTER TABLE  ' || META_INFO.table_schema || '.' || META_INFO.table_name || ' ALTER CONSTRAINT ' ||
		META_INFO.constraint_name || ' DEFERRABLE INITIALLY IMMEDIATE ';
   EXECUTE statement;

END LOOP;
return;  
end;
$$ LANGUAGE plpgsql;