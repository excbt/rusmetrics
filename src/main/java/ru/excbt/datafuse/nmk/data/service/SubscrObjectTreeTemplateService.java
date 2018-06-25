package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateRepository;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SubscrObjectTreeTemplateService {

	@Autowired
	private SubscrObjectTreeTemplateRepository subscrObjectTreeTemplateRepository;

	@Autowired
	private SubscrObjectTreeTemplateItemRepository subscrObjectTreeTemplateItemRepository;

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional( readOnly = true)
	public List<SubscrObjectTreeTemplate> selectRmaSubscriberTemplates(PortalUserIds portalUserIds) {
		checkNotNull(portalUserIds);
		if (portalUserIds.isRma()) {
			return subscrObjectTreeTemplateRepository.selectRmaSubscriberTemplates(portalUserIds.getSubscriberId());
		} else {
			return subscrObjectTreeTemplateRepository.selectSubscriberTemplates(portalUserIds.getSubscriberId());
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrObjectTreeTemplate findSubscrObjectTreeTemplate(Long id) {
		return subscrObjectTreeTemplateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrObjectTreeTemplate.class, id));
	}

	/**
	 *
	 * @param templateId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrObjectTreeTemplateItem> selectSubscrObjectTreeTemplateItems(Long templateId) {
		return subscrObjectTreeTemplateItemRepository.selectTemplateItems(templateId);
	}

}
