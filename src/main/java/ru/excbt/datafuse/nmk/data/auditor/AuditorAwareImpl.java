package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.data.repository.V_FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.V_AuditUserService;
import ru.excbt.datafuse.nmk.security.SecurityUtils;

/**
 * Компонент для аудита сущностей
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
@Profile(value = { "!" + Constants.SPRING_PROFILE_TEST })
@Component(value = "auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<V_AuditUser> {

	private final V_AuditUserService v_auditUserService;

	@Autowired
    public AuditorAwareImpl(V_AuditUserService v_auditUserService) {
        this.v_auditUserService = v_auditUserService;
    }

	@Override
	public V_AuditUser getCurrentAuditor() {
	    String username = SecurityUtils.getCurrentUserLogin();
		return v_auditUserService.findByUserName(username);
	}

}
