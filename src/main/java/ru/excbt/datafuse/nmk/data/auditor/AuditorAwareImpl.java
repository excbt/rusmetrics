package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

/**
 * Компонент для аудита сущностей
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
@Component
public class AuditorAwareImpl implements AuditorAware<AuditUser> {

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Override
	public AuditUser getCurrentAuditor() {
		return currentSubscriberService.getCurrentAuditor();
	}

}
