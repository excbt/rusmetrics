package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;

/**
 * Сервис для работы с Абонентами РМА
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.11.2015
 *
 */
@Service
public class RmaSubscriberService extends SubscriberService {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberService.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectRmaSubscribers(Long rmaSubscriberId) {
		return subscriberRepository.selectByRmaSubscriberId(rmaSubscriberId);
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public Subscriber createRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
		checkNotNull(subscriber);
		checkNotNull(rmaSubscriberId);
		checkArgument(subscriber.isNew());
		subscriber.setRmaSubscriberId(rmaSubscriberId);
		checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));
		checkArgument(subscriber.getDeleted() == 0);

		subscriber.setOrganization(findOrganization(subscriber.getOrganizationId()));

		TimezoneDef timezoneDef = timezoneDefService.findOne(subscriber.getTimezoneDefKeyname());
		subscriber.setTimezoneDef(timezoneDef);

		Subscriber resultSubscriber = subscriberRepository.save(subscriber);

		LocalDate accessDate = getSubscriberCurrentDateJoda(resultSubscriber.getId());

		subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

		reportParamsetService.createDefaultReportParamsets(resultSubscriber);

		return resultSubscriber;
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public Subscriber updateRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
		checkNotNull(subscriber);
		checkNotNull(rmaSubscriberId);
		checkArgument(!subscriber.isNew());
		subscriber.setRmaSubscriberId(rmaSubscriberId);
		checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));

		subscriber.setOrganization(findOrganization(subscriber.getOrganizationId()));

		TimezoneDef timezoneDef = timezoneDefService.findOne(subscriber.getTimezoneDefKeyname());
		subscriber.setTimezoneDef(timezoneDef);

		Subscriber checkSubscriber = subscriberRepository.findOne(subscriber.getId());
		if (checkSubscriber == null || checkSubscriber.getDeleted() == 1) {
			throw new PersistenceException(
					String.format("Subscriber (id=%d) is not found or deleted", subscriber.getId()));
		}

		return subscriberRepository.save(subscriber);
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void deleteRmaSubscriber(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);

		Subscriber subscriber = findOne(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscriberRepository.save(softDelete(subscriber));
	}

	/**
	 * 
	 * @param subscriberId
	 * @param rmaSubscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void deleteRmaSubscriberPermanent(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);

		Subscriber subscriber = findOne(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscrServiceAccessService.deleteSubscriberAccess(subscriberId);
		subscriberRepository.delete(subscriber);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectRmaSubscriberIds(Long subscriberId) {
		return subscriberRepository.selectByRmaSubscriberIds(subscriberId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectRmaList() {
		return subscriberRepository.selectRmaList();
	}

}
