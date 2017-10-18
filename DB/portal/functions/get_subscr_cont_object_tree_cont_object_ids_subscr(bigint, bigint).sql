-- Function: portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint)

-- DROP FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint);

CREATE OR REPLACE FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(
    IN p_subscriber_id bigint,
    IN p_subscr_object_tree_id bigint)
  RETURNS TABLE(cont_object_id bigint) AS
$BODY$
WITH RECURSIVE nodes(id, parent_id, object_name, path, LEVEL, cycle) AS (
		SELECT t1.id,
			t1.parent_id,
			t1.object_name,
			array [t1.id],
			1,
			false
		FROM portal.subscr_object_tree t1
		WHERE t1.id = p_subscr_object_tree_id
			--AND t1.parent_id IS NULL
			and t1.subscriber_id = p_subscriber_id
		
		UNION ALL
		
		SELECT t2.id,
			t2.parent_id,
			t2.object_name,
			nodes.path || t2.id,
			LEVEL + 1,
			t2.id = ANY (nodes.path)
		FROM portal.subscr_object_tree t2
		INNER JOIN nodes ON (nodes.id = t2.parent_id)
			AND NOT cycle
		)

SELECT co.cont_object_id
FROM portal.subscr_object_tree_cont_object co
WHERE co.subscr_object_tree_id IN (
		SELECT n.id
		FROM nodes n limit 200
		);$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint) TO public;
GRANT EXECUTE ON FUNCTION portal.get_subscr_cont_object_tree_cont_object_ids_subscr(bigint, bigint) TO portal_role;
