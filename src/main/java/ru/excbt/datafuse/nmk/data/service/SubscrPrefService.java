package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrTypePref;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;
import ru.excbt.datafuse.nmk.data.repository.SubscrTypePrefRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.SubscrPrefRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPrefService extends AbstractService implements SecuredRoles {

	@Autowired
	private SubscrPrefRepository subscrPrefRepository;

	@Autowired
	private SubscrTypePrefRepository subscrTypePrefRepository;

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

		return result;
	}

}
