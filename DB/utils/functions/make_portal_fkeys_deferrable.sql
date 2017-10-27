CREATE OR REPLACE FUNCTION utils.make_portal_fkeys_deferrable() RETURNS void AS $$
declare
   tables RECORD;
   statement TEXT;
begin
FOR tables IN 
	SELECT table_schema, table_name
	FROM information_schema.tables t INNER JOIN information_schema.schemata s 
	ON s.schema_name = t.table_schema 
	WHERE t.table_schema NOT IN ('pg_catalog', 'information_schema')
	and t.table_schema IN ('portal', 'public')
	AND t.table_type NOT IN ('VIEW')
	ORDER BY table_schema || '.' || table_name			
LOOP
   statement := 'SELECT utils.make_fkeys_deferrable(''' || tables.table_schema || ''',''' || tables.table_name || ''')';
   EXECUTE statement;
END LOOP;
return;  
end;
$$ LANGUAGE plpgsql;