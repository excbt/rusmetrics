package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.constant.SecurityConstraints;
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
@Component(value = "auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<Long> {

	@Override
	public Long getCurrentAuditor() {
        SubscriberUserDetails userDetails = SecurityUtils.getPortalUserDetails();
		return (userDetails == null) ? SecurityConstraints.SYSTEM_ID :
            userDetails.getId();
	}

}
