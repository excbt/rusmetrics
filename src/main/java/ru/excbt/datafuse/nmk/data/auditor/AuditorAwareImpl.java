package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.data.constant.SecurityConstraints;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

import java.util.Optional;

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
	public Optional<Long> getCurrentAuditor() {
        Optional<SubscriberUserDetails> userDetailsOpt = Optional.ofNullable(SecurityUtils.getPortalUserDetails());
		return Optional.of(userDetailsOpt.map(SubscriberUserDetails::getId).orElse(SecurityConstraints.SYSTEM_ID));
	}

}
