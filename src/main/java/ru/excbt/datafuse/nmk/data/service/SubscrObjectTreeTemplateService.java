package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;

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
		return subscrObjectTreeTemplateRepository.findOne(id);
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
