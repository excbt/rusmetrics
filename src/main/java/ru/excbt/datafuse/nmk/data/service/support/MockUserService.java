package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.AuditUserService;

@Service
public class MockUserService {

	private static final Logger logger = LoggerFactory
			.getLogger(MockUserService.class);

	private Long mockUserId = null;

	@Autowired
	private AuditUserService auditUserService;

	/**
	 * 
	 * @return
	 */
	public Long getMockUserId() {
		return mockUserId;
	}

	/**
	 * 
	 * @param mockUserId
	 */
	public void setMockUserId(Long mockUserId) {
		this.mockUserId = mockUserId;
	}

	/**
	 * 
	 * @return
	 */
	public AuditUser getMockAuditUser() {
		checkState(mockUserId != null, "Mock User Service is Disabled");

		logger.warn("ATTENTION!!! Using MockUser");
		AuditUser result = auditUserService.findOne(mockUserId);
		return result;
	}

}
