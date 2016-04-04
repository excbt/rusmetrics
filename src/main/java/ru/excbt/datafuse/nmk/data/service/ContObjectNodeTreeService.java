package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;
import ru.excbt.datafuse.nmk.data.repository.ContObjectNodeTreeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ContObjectNodeTreeService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectNodeTreeService.class);

	public static final String NODE_TREE_TYPE_1 = "TYPE_1";

	@Autowired
	private ContObjectNodeTreeRepository contObjectNodeTreeRepository;

	/**
	 * 
	 * @param entity
	 * @return
	 */
	private ContObjectNodeTree saveNode(ContObjectNodeTree entity) {
		ContObjectNodeTree result = contObjectNodeTreeRepository.save(entity);

		//		if (result.getChildNodeList() != null) {
		//			result.getChildNodeList().forEach(i -> {
		//				saveNode(i);
		//			});
		//		}
		return result;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ContObjectNodeTree saveRootNode(ContObjectNodeTree entity) {
		checkArgument(entity.getParent() == null);

		return saveNode(entity);

	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	public ContObjectNodeTree newRootContObjectNode(ContObject contObject) {
		ContObjectNodeTree root = new ContObjectNodeTree();

		root.setContObjectId(contObject.getId());
		root.setNodeName(contObject.getName());
		root.setNodeTreeType(NODE_TREE_TYPE_1);

		return root;

	}

	/**
	 * 
	 * @param node
	 * @param nodeName
	 * @return
	 */
	public ContObjectNodeTree addChildNode(ContObjectNodeTree node, String nodeName) {
		assertNotNull(node);
		ContObjectNodeTree child = new ContObjectNodeTree();
		child.setNodeName(nodeName);
		child.setParent(node);
		child.setNodeTreeType(node.getNodeTreeType());
		node.getChildNodeList().add(child);
		return child;
	}

}
