package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeTemplateRepository;

@Service
public class SubscrObjectTreeTemplateService {

	@Autowired
	private SubscrObjectTreeTemplateRepository subscrObjectTreeTemplateRepository;

	/**
	 * 
	 * @param rmaSubscriberTemplate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrObjectTreeTemplate> selectRmaSubscriberTemplates(Long rmaSubscriberId) {
		return subscrObjectTreeTemplateRepository.selectRmaSubscriberTemplates(rmaSubscriberId);
	}

}
