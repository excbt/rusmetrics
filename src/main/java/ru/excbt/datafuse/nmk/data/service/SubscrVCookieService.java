package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.repository.SubscrVCookieRepository;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

@Service
public class SubscrVCookieService {

	private static final Logger logger = LoggerFactory.getLogger(SubscrVCookieService.class);

	@Autowired
	private SubscrVCookieRepository subscrVCookieRepository;

	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrVCookie> selectSubscrVCookie(SubscriberParam subscriberParam) {
		List<SubscrVCookie> result = subscrVCookieRepository
				.selectSubscrVCookie(subscriberParam.getSubscriberId());
		return result;
	}

	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrVCookie> selectSubscrVCookieByUser(SubscriberParam subscriberParam) {
		List<SubscrVCookie> result = subscrVCookieRepository
				.selectSubscrVCookie(subscriberParam.getSubscriberId(), subscriberParam.getSubscrUserId());
		return result;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrVCookie saveSubscrVCookie(SubscrVCookie entity) {
		checkNotNull(entity.getSubscriberId());
		checkNotNull(entity.getVcMode());
		checkNotNull(entity.getVcKey());

		checkArgument(entity.getSubscrUserId() == null);

		List<SubscrVCookie> checkList = subscrVCookieRepository.selectSubscrVCookie(entity.getSubscriberId());

		return _saveVCookieSubscrUser(entity, checkList);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrVCookie saveSubscrVCookieUser(SubscrVCookie entity) {
		checkNotNull(entity.getSubscriberId());
		checkNotNull(entity.getSubscrUserId());
		checkNotNull(entity.getVcMode());
		checkNotNull(entity.getVcKey());

		List<SubscrVCookie> checkList = subscrVCookieRepository
				.selectSubscrVCookie(entity.getSubscriberId(), entity.getSubscrUserId());

		return _saveVCookieSubscrUser(entity, checkList);
	}

	/**
	 * 
	 * @param entity
	 * @param existingVCList
	 * @return
	 */
	private SubscrVCookie _saveVCookieSubscrUser(SubscrVCookie entity,
			final List<SubscrVCookie> existingVCList) {
		checkNotNull(entity.getSubscriberId());
		checkNotNull(entity.getVcMode());
		checkNotNull(entity.getVcKey());

		//checkArgument(entity.getSubscrUserId() == null);

		checkNotNull(existingVCList);

		List<SubscrVCookie> existingVC = existingVCList.stream()
				.filter(i -> entity.getVcMode().equals(i.getVcMode()) && entity.getVcKey().equals(i.getVcKey()))
				.collect(Collectors.toList());

		if (existingVC.size() == 1) {
			entity.setId(existingVC.get(0).getId());
			entity.setVersion(existingVC.get(0).getVersion());
		}

		return subscrVCookieRepository.save(entity);
	}

	/**
	 * 
	 * @param entities
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrVCookie> saveVCookieSubscr(List<SubscrVCookie> entities) {

		Long subscriberId = entities.isEmpty() ? null : entities.get(0).getSubscriberId();

		List<SubscrVCookie> checkList = subscrVCookieRepository.selectSubscrVCookie(subscriberId);

		for (SubscrVCookie vc : entities) {
			_saveVCookieSubscrUser(vc, checkList);
		}
		return Lists.newArrayList(subscrVCookieRepository.save(entities));
	}

}
