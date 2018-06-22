package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryNodeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.Optional;

/**
 * Сервис для работы с узлами универсального справочника
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
@Service
public class UDirectoryNodeService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryNodeService.class);

	@Autowired
	private UDirectoryNodeRepository directoryNodeRepository;

	@Autowired
	private UDirectoryService directoryService;

	// @Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	// public UDirectoryNode save(final UDirectoryNode nodeDir) {
	// checkNotNull(nodeDir);
	// UDirectoryNode result = directoryNodeRepository.save(nodeDir);
	// loadLazyChildNodes(result);
	// return result;
	// }

	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public UDirectoryNode save(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);
		UDirectoryNode result = directoryNodeRepository.save(nodeDir);
		loadLazyChildNodes(result);
		return result;
	}

	/**
	 *
	 * @param nodeDir
	 * @param directoryId
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public UDirectoryNode saveWithDictionary(long subscriberId, final UDirectoryNode nodeDir, final long directoryId) {
		checkNotNull(nodeDir);
		checkArgument(directoryId > 0);

		UDirectory directory = directoryService.findOne(subscriberId, directoryId);

		if (directory == null) {
			logger.warn("UDirectory (id={}) is not found", directoryId);
			throw new PersistenceException();
		}

		UDirectoryNode result = directoryNodeRepository.save(nodeDir);
		loadLazyChildNodes(result);

		directory.setDirectoryNode(result);

		return result;
	}

	@Transactional( propagation = Propagation.REQUIRED)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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

	/**
	 *
	 * @param nodeId
	 * @return
	 */
	@Transactional( readOnly = true)
	public UDirectoryNode getRootNode(long nodeId) {
		Optional<UDirectoryNode> nodeOpt = directoryNodeRepository.findById(nodeId);

		if (!nodeOpt.isPresent()) {
			return null;
		}

		if (!nodeOpt.get().isRoot()) {
			throw new IllegalArgumentException("Argument id = " + nodeId + " is not root element of Node Directory");
		}
		loadLazyChildNodes(nodeOpt.get());
		return nodeOpt.get();
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public UDirectoryNode findOne(long id) {
		UDirectoryNode result = directoryNodeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UDirectoryNode.class, id));
		loadLazyChildNodes(result);
		return result;
	}

	/**
	 *
	 * @param nodeDirectory
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void delete(final UDirectoryNode nodeDirectory) {
		checkNotNull(nodeDirectory);
		checkNotNull(nodeDirectory.getId());
		directoryNodeRepository.deleteById(nodeDirectory.getId());
	}

	/**
	 *
	 * @param nodeDir
	 */
	@Transactional
	public void loadLazyChildNodes(final UDirectoryNode nodeDir) {
		checkNotNull(nodeDir);

		for (UDirectoryNode child : nodeDir.getChildNodes()) {
			loadLazyChildNodes(child);
		}

	}

}
