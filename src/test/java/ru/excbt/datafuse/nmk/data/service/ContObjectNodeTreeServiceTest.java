package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;

public class ContObjectNodeTreeServiceTest extends JpaSupportTest {

	@Autowired
	private ContObjectNodeTreeService contObjectNodeTreeService;

	@Autowired
	private ContObjectService contObjectService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveNode() throws Exception {

		ContObject contObject = contObjectService.findContObject(725L);

		assertNotNull(contObject);

		ContObjectNodeTree root = contObjectNodeTreeService.newRootContObjectNode(contObject);

		contObjectNodeTreeService.addChildNode(root, "Этаж 1");
		ContObjectNodeTree floor2 = contObjectNodeTreeService.addChildNode(root, "Этаж 2");
		contObjectNodeTreeService.addChildNode(root, "Этаж 3");

		for (int i = 1; i <= 50; i++) {
			contObjectNodeTreeService.addChildNode(floor2, "Кв " + i);
		}

		root = contObjectNodeTreeService.saveRootNode(root);

	}

}
