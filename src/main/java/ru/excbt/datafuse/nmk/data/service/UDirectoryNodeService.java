package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryNodeRepository;

@Service
@Transactional
public class UDirectoryNodeService implements SecuredServiceRoles {

	@Autowired
	private UDirectoryNodeRepository directoryNodeRepository;
	
	@Autowired
	private UDirectoryService directoryService;

//	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
//	public UDirectoryNode save(final UDirectoryNode nodeDir) {
//		checkNotNull(nodeDir);
//		UDirectoryNode result = directoryNodeRepository.save(nodeDir); 
//		loadLazyChildNodes(result);
//		return result; 
//	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public UDirectoryNode save(final UDirectoryNode nodeDir, final long directoryId) {
		checkNotNull(nodeDir);
		checkArgument(directoryId > 0);
		
		UDirectory directory = directoryService.findOne(directoryId);
		if (directory == null) {
			throw new PersistenceException();
		}
		
		UDirectoryNode result = directoryNodeRepository.save(nodeDir);
		directory.setDirectoryNode(result);
		directoryService.save(directory);
		loadLazyChildNodes(result);
		return result; 
	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWithChildren(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);

		UDirectoryNode savedND = directoryNodeRepository.save(nodeDir);
		if (nodeDir.getChildNodes() == null) {
			return;
		}
		for (UDirectoryNode nd : nodeDir.getChildNodes()) {
			nd.setParentId(savedND.getId());
			saveWithChildren(nd);
		}
	}

	@Transactional(readOnly = true)
	public UDirectoryNode getRootNode(long nodeId) {
		UDirectoryNode result = directoryNodeRepository.findOne(nodeId);
		
		if (result == null) {
			return null;
		}
		
		if (!result.isRoot()) {
			throw new IllegalArgumentException("Argument id = " + nodeId
					+ " is not root element of Node Directory");
		}
		loadLazyChildNodes(result);
		return result;
	}

	@Transactional(readOnly = true)
	public UDirectoryNode findOne(long id) {
		UDirectoryNode result = directoryNodeRepository.findOne(id);
		loadLazyChildNodes(result);
		return result;
	}

	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(final UDirectoryNode nodeDirectory) {
		checkNotNull(nodeDirectory);
		checkNotNull(nodeDirectory.getId());
		directoryNodeRepository.delete(nodeDirectory.getId());
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
