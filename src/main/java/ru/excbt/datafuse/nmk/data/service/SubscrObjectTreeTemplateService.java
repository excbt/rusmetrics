package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

@Service
public class SubscrObjectTreeTemplateService {

	@Autowired
	private SubscrObjectTreeTemplateRepository subscrObjectTreeTemplateRepository;

	@Autowired
	private SubscrObjectTreeTemplateItemRepository subscrObjectTreeTemplateItemRepository;

	/**
	 * 
	 * @param rmaSubscriberTemplate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrObjectTreeTemplate> selectRmaSubscriberTemplates(SubscriberParam subscriberParam) {
		checkNotNull(subscriberParam);
		if (subscriberParam.isRma()) {
			return subscrObjectTreeTemplateRepository.selectRmaSubscriberTemplates(subscriberParam.getSubscriberId());
		} else {
			return subscrObjectTreeTemplateRepository.selectSubscriberTemplates(subscriberParam.getSubscriberId());
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrObjectTreeTemplate findSubscrObjectTreeTemplate(Long id) {
		return subscrObjectTreeTemplateRepository.findOne(id);
	}

	/**
	 * 
	 * @param templateId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrObjectTreeTemplateItem> selectSubscrObjectTreeTemplateItems(Long templateId) {
		return subscrObjectTreeTemplateItemRepository.selectTemplateItems(templateId);
	}

}
