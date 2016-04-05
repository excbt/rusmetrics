package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaContObjectNodeTreeControllerTest extends RmaControllerTest {

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

	@Test
	public void testGetByContObject() throws Exception {
		_testGetJson("/api/rma/contObjectNodeTree/byContObject/725");
	}

	@Test
	public void testGetByContObject_512103284() throws Exception {
		_testGetJson("/api/rma/contObjectNodeTree/byContObject/512103284");
	}

}
