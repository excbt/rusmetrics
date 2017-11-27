package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.constant.SecurityConstraints;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

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

    public AuditorAwareImpl() {
    }

	@Override
	public V_AuditUser getCurrentAuditor() {
        SubscriberUserDetails userDetails = SecurityUtils.getPortalUserDetails();
		return (userDetails == null) ? new V_AuditUser().id(SecurityConstraints.SYSTEM_ID) :
            new V_AuditUser().id(userDetails.getId());
	}

}
