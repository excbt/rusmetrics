package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.repository.NodeDirectoryRepository;

@Service
@Transactional
public class SubscrOrgService {

	@Autowired
	private NodeDirectoryRepository nodeDirectoryRepository;
	
	@Transactional(readOnly = true)
	public List<NodeDirectory> selectNodeDirectories(long subscrOrgId) {
		return nodeDirectoryRepository.selectBySubscrOrg(subscrOrgId);
	}
	
	
}
