package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;

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

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	private boolean isMock;

	@Override
	public V_AuditUser getCurrentAuditor() {
		return currentSubscriberService.getCurrentAuditor();
	}

}
