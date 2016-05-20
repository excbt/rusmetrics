package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefObjectTreeType;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.model.SubscrTypePref;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.SubscrPrefObjectTreeTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrPrefValueRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrTypePrefRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.SubscrPrefRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPrefService extends AbstractService implements SecuredRoles {

	public final static String SUBSCR_TYPE_NORMAL = SubscrTypeKey.NORMAL.getKeyname();

	public final static String SUBSCR_OBJECT_TREE = "SUBSCR_OBJECT_TREE";

	@Autowired
	private SubscrPrefRepository subscrPrefRepository;

	@Autowired
	private SubscrTypePrefRepository subscrTypePrefRepository;

	@Autowired
	private SubscrPrefValueRepository subscrPrefValueRepository;

	@Autowired
	private SubscrPrefObjectTreeTypeRepository subscrPrefObjectTreeTypeRepository;

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
	public List<SubscrPref> selectSubscrPrefsBySubscrType(String subscrTypeKeyname) {
		List<SubscrTypePref> typePrefList = subscrTypePrefRepository.findBySubscrType(subscrTypeKeyname);

		List<String> subsctPrefKeynames = typePrefList.stream().map(t -> t.getSubscrPref())
				.collect(Collectors.toList());

		List<SubscrPref> subsctPrefList = Lists.newArrayList(subscrPrefRepository.selectSubscrPref());

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
	public List<SubscrPrefValue> selectSubscrPrefValue(SubscriberParam subscriberParam) {
		checkNotNull(subscriberParam);

		SubscrTypeKey subscrTypeKey = getSubscrTypeKey(subscriberParam.getSubscriberId());

		List<SubscrPrefValue> prefValueList = subscrPrefValueRepository
				.selectSubscrPrefValue(subscriberParam.getSubscriberId());

		List<SubscrPrefValue> result = filterSubscriberPrefValues(subscriberParam.getSubscriberId(),
				subscrTypeKey.getKeyname(), prefValueList);

		//Collections.sort(result, SUBSCR_PREF_COMPARATOR);

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param prefValueList
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPrefValue> saveSubscrPrefValues(SubscriberParam subscriberParam,
			List<SubscrPrefValue> prefValueList) {
		checkNotNull(subscriberParam);
		checkNotNull(prefValueList);

		SubscrTypeKey subscrTypeKey = getSubscrTypeKey(subscriberParam.getSubscriberId());

		List<SubscrPrefValue> result = filterSubscriberPrefValues(subscriberParam.getSubscriberId(),
				subscrTypeKey.getKeyname(), prefValueList);
		return Lists.newArrayList(subscrPrefValueRepository.save(result));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	private String getSubscriberType(Long subscriberId) {
		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber (id=%d) is not found", subscriberId));
		}

		String subscrType = subscriber.getSubscrType();
		if (subscrType == null) {
			subscrType = SUBSCR_TYPE_NORMAL;
		}

		SubscrTypeKey key = SubscrTypeKey.searchKeyname(subscrType);
		if (key == null) {

			throw new PersistenceException(String.format("subscrType (%s) is not supported", subscrType));

		}

		return subscrType;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	private SubscrTypeKey getSubscrTypeKey(Long subscriberId) {
		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("Subscriber (id=%d) is not found", subscriberId));
		}

		String subscrType = subscriber.getSubscrType();
		if (subscrType == null) {
			return SubscrTypeKey.NORMAL;
		}

		SubscrTypeKey key = SubscrTypeKey.searchKeyname(subscrType);
		if (key == null) {

			throw new PersistenceException(String.format("subscrType (%s) is not supported", subscrType));

		}

		return key;
	}

	/**
	 * 
	 * @param prefValueList
	 * @return
	 */
	private List<SubscrPrefValue> filterSubscriberPrefValues(Long subscriberId, String subscrTypeKeyname,
			List<SubscrPrefValue> prefValueList) {

		List<SubscrPrefValue> result = new ArrayList<>(prefValueList);

		List<SubscrPref> subscrPrefList = selectSubscrPrefsBySubscrType(subscrTypeKeyname);

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

		return result;
	}

	/**
	 * 
	 * @param subscrPrefKeyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<String> selectSubscrPrefTreeTypes(String subscrPrefKeyname) {
		SubscrPref subscrPref = subscrPrefRepository.findOne(subscrPrefKeyname);

		if (subscrPref == null) {
			return new ArrayList<>();
		}

		if (!SUBSCR_OBJECT_TREE.equals(subscrPref.getSubscrPrefCategory())) {
			return new ArrayList<>();
		}

		List<SubscrPrefObjectTreeType> treeTypes = subscrPrefObjectTreeTypeRepository
				.findBySubscrPrefKeyname(subscrPrefKeyname);

		Collection<String> result = treeTypes.stream().map(i -> i.getObjectTreeType()).collect(Collectors.toSet());

		return Lists.newArrayList(result);
	}

}
