package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaContObjectNodeTreeControllerTest extends RmaControllerTest {

	/**
	 * 
	 * @param node
	 */
	private void updateDevComment(ContObjectNodeTree node) {
		node.setDevComment("Updated by REST" + System.currentTimeMillis());
		if (node.getChildNodeList() != null) {
			node.getChildNodeList().forEach(i -> {
				updateDevComment(i);
			});
		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCRUDNodeTree() throws Exception {
		String content = _testGetJson("/api/rma/contObjectNodeTree/512105299");

		ContObjectNodeTree root = fromJSON(new TypeReference<ContObjectNodeTree>() {
		}, content);

		updateDevComment(root);

		_testUpdateJson("/api/rma/contObjectNodeTree/512105299", root);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetByContObject() throws Exception {
		_testGetJson("/api/rma/contObjectNodeTree/byContObject/725");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCRUD_ByContObject_512103284() throws Exception {
		String content = _testGetJson("/api/rma/contObjectNodeTree/byContObject/512103284");
		List<ContObjectNodeTree> nodeList = fromJSON(new TypeReference<List<ContObjectNodeTree>>() {
		}, content);

		assertFalse(nodeList.isEmpty());

		Long id = _testCreateJson("/api/rma/contObjectNodeTree/byContObject/512103284", nodeList.get(0));

		content = _testGetJson("/api/rma/contObjectNodeTree/" + id);

		ContObjectNodeTree root = fromJSON(new TypeReference<ContObjectNodeTree>() {
		}, content);

		updateDevComment(root);

		_testUpdateJson("/api/rma/contObjectNodeTree/" + id, root);

		_testDeleteJson("/api/rma/contObjectNodeTree/" + id);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCRUD_512103284() throws Exception {
		String content = _testGetJson("/api/rma/contObjectNodeTree/byContObject/512103284");
		List<ContObjectNodeTree> nodeList = fromJSON(new TypeReference<List<ContObjectNodeTree>>() {
		}, content);

		assertFalse(nodeList.isEmpty());

		Long id = _testCreateJson("/api/rma/contObjectNodeTree", nodeList.get(0));

		content = _testGetJson("/api/rma/contObjectNodeTree/" + id);

		ContObjectNodeTree root = fromJSON(new TypeReference<ContObjectNodeTree>() {
		}, content);

		updateDevComment(root);

		_testUpdateJson("/api/rma/contObjectNodeTree/" + id, root);

		_testDeleteJson("/api/rma/contObjectNodeTree/" + id);

	}

}
