package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.V_AuditUserService;

/**
 * Класс "заглушка" для работы с пользователем
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@Service
public class MockUserService {

	private static final Logger logger = LoggerFactory.getLogger(MockUserService.class);

	private Long mockUserId = null;

//	@Autowired
//	private V_AuditUserService auditUserService;

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
	public V_AuditUser getMockAuditUser() {
		checkState(mockUserId != null, "Mock User Service is Disabled");

		logger.warn("ATTENTION!!! Using MockUser");
		V_AuditUser result = new V_AuditUser().id(mockUserId);
		return result;
	}

	/**
	 *
	 * @return
	 */
	public boolean isMockUserEnabled() {
		return mockUserId != null;
	}

	/**
	 *
	 * @return
	 */
	public SubscrUser getMockSubscrUser() {
		checkState(mockUserId != null, "Mock User Service is Disabled");
		SubscrUser result = new SubscrUser();

		result.setId(mockUserId);
		result.setUserName("Mock User");
		return result;

	}

}
