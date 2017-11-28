package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;


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

	public static final long SYSTEM_USER = 1000L;

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
	public boolean isMockUserEnabled() {
		return mockUserId != null;
	}

	/**
	 *
	 * @return
	 */
	public SubscrUser getMockSubscrUser() {
	    if (mockUserId == null) {
	        return null;
        }
		//checkState(mockUserId != null, "Mock User Service is Disabled");
		SubscrUser result = new SubscrUser();

		result.setId(mockUserId);
		result.setUserName("Mock User");
		return result;

	}

}
