package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventTypeActionRepository;

/**
 * Сервис для работы с действиями для типа уведомлений
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@Service
public class SubscrContEventTypeActionService {

	@Autowired
	private ContEventTypeRepository contEventTypeRepository;

	@Autowired
	private SubscrContEventTypeActionRepository subscrContEventTypeActionRepository;

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContEventType> selectAvailableContEventTypes() {
		return contEventTypeRepository.selectBySmsNotification();
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrContEventTypeAction> selectSubscrContEventTypeActions(Long subscriberId, Long contEventTypeId) {
		List<SubscrContEventTypeAction> preResult = subscrContEventTypeActionRepository
				.selectSubscrContEventActions(subscriberId, contEventTypeId);
		return ObjectFilters.deletedFilter(preResult);
	}

    /**
     *
     * @param subscriber
     * @param contEventType
     * @param subscrContEventTypeActions
     * @return
     */
	@Transactional
	public List<SubscrContEventTypeAction> updateSubscrContEventTypeActions(Subscriber subscriber,
			ContEventType contEventType, List<SubscrContEventTypeAction> subscrContEventTypeActions) {

		checkNotNull(subscriber);
		checkNotNull(contEventType);
		checkNotNull(subscrContEventTypeActions);

		List<SubscrContEventTypeAction> currentActions = subscrContEventTypeActionRepository
				.selectSubscrContEventActions(subscriber.getId(), contEventType.getId());

		subscrContEventTypeActions.forEach(i -> {
			i.setContEventType(contEventType);
			i.setSubscriber(subscriber);
			checkNotNull(i.getSubscrActionUserId());

			Optional<SubscrContEventTypeAction> currActionOption = currentActions.stream()
					.filter(ca -> ca.getSubscrActionUserId().equals(i.getSubscrActionUserId())).findFirst();

			if (currActionOption.isPresent()) {
				currActionOption.get().setIsEmail(i.getIsEmail());
				currActionOption.get().setIsSms(i.getIsSms());
			} else {
				SubscrContEventTypeAction newAction = new SubscrContEventTypeAction();
				newAction.setContEventType(contEventType);
				newAction.setSubscriber(subscriber);
				newAction.setSubscrActionUserId(i.getSubscrActionUserId());
				newAction.setIsEmail(i.getIsEmail());
				newAction.setIsSms(i.getIsSms());
				currentActions.add(newAction);
			}

		});

		List<SubscrContEventTypeAction> result = Lists
				.newArrayList(subscrContEventTypeActionRepository.saveAll(currentActions));

		return result;
	}

}
