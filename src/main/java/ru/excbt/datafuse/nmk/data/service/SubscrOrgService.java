package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryNodeRepository;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryRepository;

@Service
@Transactional
public class SubscrOrgService {

	@Autowired
	private UDirectoryNodeRepository directoryNodeRepository;
	
	@Autowired
	private UDirectoryRepository directoryRepository;
	
	@Transactional(readOnly = true)
	public List<UDirectory> selectNodeDirectories(long subscrOrgId) {
		return directoryRepository.selectBySubscrOrg(subscrOrgId);
	}
	
	
}
