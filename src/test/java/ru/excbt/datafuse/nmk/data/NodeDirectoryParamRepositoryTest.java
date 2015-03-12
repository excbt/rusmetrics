package ru.excbt.datafuse.nmk.data;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.constant.ParamType;
import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.model.NodeDirectoryParam;
import ru.excbt.datafuse.nmk.data.repository.NodeDirectoryParamRepository;
import ru.excbt.datafuse.nmk.data.service.NodeDirectoryService;

public class NodeDirectoryParamRepositoryTest extends RepositoryTest {

	private static final Logger logger = LoggerFactory
			.getLogger(NodeDirectoryParamRepositoryTest.class);

	@Autowired
	private NodeDirectoryParamRepository directoryRepositoryParam;

	@Autowired
	private NodeDirectoryService nodeDirectoryService;

	@Test
	public void test() {
		NodeDirectory n = nodeDirectoryService
				.findOne(NodeDirectoryServiceTest.TEST_DIRECTORY_ID);

		NodeDirectoryParam p = new NodeDirectoryParam();
		p.setParamType(ParamType.STRING.name());
		p.setParamName("Name1");
		p.setDirectory(n);
		
		directoryRepositoryParam.save(p);

	}

}
