package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.repository.NodeDirectoryRepository;

@Service
@Transactional
public class NodeDirectoryService {

	@Autowired
	private NodeDirectoryRepository nodeDirectoryRepository;

	
	@Secured({ "ROLE_ADMIN", "ROLE_SUBSCR_ADMIN" })
	public NodeDirectory save(final NodeDirectory nodeDir) {
		checkNotNull(nodeDir);
		nodeDir.getRowAudit().setModifiedAt(DateTime.now());
		return nodeDirectoryRepository.save(nodeDir);
	}

	
	@Secured({ "ROLE_ADMIN", "ROLE_SUBSCR_ADMIN" })
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWithChildren(final NodeDirectory nodeDir) {
		checkNotNull(nodeDir);
		nodeDir.getRowAudit().setModifiedAt(DateTime.now());
		
		NodeDirectory savedND = nodeDirectoryRepository.save(nodeDir);
		if (nodeDir.getChildNodes() == null) {
			return;
		}
		for (NodeDirectory nd : nodeDir.getChildNodes()) {
			nd.setParentId(savedND.getId());
			saveWithChildren(nd);
		}
	}
	
	
	@Transactional(readOnly = true)
	public NodeDirectory getRootNode(long id) {
		NodeDirectory result = nodeDirectoryRepository.findOne(id);
		if (!result.isRoot()) {
			throw new IllegalArgumentException("Argument id = " + id + " is not root element of Node Directory");
		}
		return result;
	}

	@Secured({ "ROLE_ADMIN", "ROLE_SUBSCR_ADMIN" })
	public void delete (final NodeDirectory nodeDirectory) {
		checkNotNull(nodeDirectory);
		checkNotNull(nodeDirectory.getId());
		nodeDirectoryRepository.delete(nodeDirectory.getId());
	}
	
	
}
