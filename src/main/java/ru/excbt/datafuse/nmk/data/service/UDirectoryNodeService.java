package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryNodeRepository;

@Service
@Transactional
public class UDirectoryNodeService implements SecuredServiceRoles {

	@Autowired
	private UDirectoryNodeRepository nodeDirectoryRepository;

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public UDirectoryNode save(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);
		UDirectoryNode result = nodeDirectoryRepository.save(nodeDir); 
		loadLazyChildNodes(result);
		return result; 
	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWithChildren(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);

		UDirectoryNode savedND = nodeDirectoryRepository.save(nodeDir);
		if (nodeDir.getChildNodes() == null) {
			return;
		}
		for (UDirectoryNode nd : nodeDir.getChildNodes()) {
			nd.setParentId(savedND.getId());
			saveWithChildren(nd);
		}
	}

	@Transactional(readOnly = true)
	public UDirectoryNode getRootNode(long id) {
		UDirectoryNode result = nodeDirectoryRepository.findOne(id);
		if (!result.isRoot()) {
			throw new IllegalArgumentException("Argument id = " + id
					+ " is not root element of Node Directory");
		}
		loadLazyChildNodes(result);
		return result;
	}

	@Transactional(readOnly = true)
	public UDirectoryNode findOne(long id) {
		UDirectoryNode result = nodeDirectoryRepository.findOne(id);
		loadLazyChildNodes(result);
		return result;
	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(final UDirectoryNode nodeDirectory) {
		checkNotNull(nodeDirectory);
		checkNotNull(nodeDirectory.getId());
		nodeDirectoryRepository.delete(nodeDirectory.getId());
	}

	/**
	 * 
	 * @param nodeDir
	 */
	public void loadLazyChildNodes(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);
		
		for (UDirectoryNode child : nodeDir.getChildNodes()) {
			loadLazyChildNodes(child);
		}

	}

}
