package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.model.SubscrTypePref;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;
import ru.excbt.datafuse.nmk.data.repository.SubscrPrefValueRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrTypePrefRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.SubscrPrefRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPrefService extends AbstractService implements SecuredRoles {

	public final static String SUBSCR_TYPE_NORMAL = "NORMAL";

	@Autowired
	private SubscrPrefRepository subscrPrefRepository;

	@Autowired
	private SubscrTypePrefRepository subscrTypePrefRepository;

	@Autowired
	private SubscrPrefValueRepository subscrPrefValueRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 */
	public static Comparator<SubscrPrefValue> SUBSCR_PREF_COMPARATOR = new Comparator<SubscrPrefValue>() {
		@Override
		public int compare(SubscrPrefValue o1, SubscrPrefValue o2) {

			String category1 = o1.getSubscrPrefCategory();
			String category2 = o2.getSubscrPrefCategory();
			int sComp = category1.compareTo(category2);
			if (sComp != 0) {
				return sComp;
			} else {
				String pref1 = o1.getSubscrPrefKeyname();
				String pref2 = o2.getSubscrPrefKeyname();
				return pref1.compareTo(pref2);
			}
		}
	};

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPref> selectSubscrPrefsBySubscrType(String subscrType) {
		List<SubscrTypePref> typePrefList = subscrTypePrefRepository.findBySubscrType(subscrType);

		List<String> subsctPrefKeynames = typePrefList.stream().map(t -> t.getSubscrPref())
				.collect(Collectors.toList());

		List<SubscrPref> subsctPrefList = Lists.newArrayList(subscrPrefRepository.findAll());

		List<SubscrPref> result = subsctPrefList.stream().filter(p -> subsctPrefKeynames.contains(p.getKeyname()))
				.collect(Collectors.toList());

		return ObjectFilters.disabledFilter(result);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPrefValue> selectSubscrPrefValue(Long subscriberId) {
		checkNotNull(subscriberId);

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber (id=%d) is not found", subscriberId));
		}

		String subscrType = subscriber.getSubscrType();
		if (subscrType == null) {
			subscrType = SUBSCR_TYPE_NORMAL;
		}

		List<SubscrPrefValue> prefValueList = subscrPrefValueRepository.selectSubscrPrefValue(subscriberId);

		List<SubscrPrefValue> result = new ArrayList<>(prefValueList);

		List<SubscrPref> subscrPrefList = selectSubscrPrefsBySubscrType(subscrType);
		List<String> subsctPrefKeynames = subscrPrefList.stream().map(t -> t.getKeyname()).collect(Collectors.toList());
		List<String> valueSubsctPrefKeynames = prefValueList.stream().map(t -> t.getSubscrPrefKeyname())
				.collect(Collectors.toList());

		for (SubscrPrefValue value : prefValueList) {
			if (!subsctPrefKeynames.contains(value.getSubscrPrefKeyname())) {
				result.remove(value);
			}
		}

		for (SubscrPref pref : subscrPrefList) {
			if (!valueSubsctPrefKeynames.contains(pref.getKeyname())) {
				SubscrPrefValue newValue = new SubscrPrefValue();
				newValue.setSubscriberId(subscriberId);
				newValue.setSubscrPrefCategory(pref.getSubscrPrefCategory());
				newValue.setSubscrPrefKeyname(pref.getKeyname());
				newValue.setSubscrPref(pref);
				result.add(newValue);
			}
		}

		Collections.sort(result, SUBSCR_PREF_COMPARATOR);

		return result;
	}

}
