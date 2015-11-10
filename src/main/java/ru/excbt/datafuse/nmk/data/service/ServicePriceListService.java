package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ServicePriceList;
import ru.excbt.datafuse.nmk.data.repository.ServicePriceListRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ServicePriceListService implements SecuredRoles {

	private final static String PRICE_LIST_PREFIX = "Копия ";

	private final static int PRICE_LEVEL_NMC = 0;
	private final static int PRICE_LEVEL_RMA = 1;

	@Autowired
	private ServicePriceListRepository servicePriceListRepository;

	/**
	 * 
	 * @param level
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	List<ServicePriceList> findRootPriceLists() {
		return servicePriceListRepository.findByLevel(0);
	}

	/**
	 * 
	 * @param srcServicePriceListId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public ServicePriceList makeRmaPriceList(Long srcServicePriceListId, Long subscriberId) {
		checkNotNull(srcServicePriceListId);

		ServicePriceList srcPriceList = servicePriceListRepository.findOne(srcServicePriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(
					String.format("ServicePriceList (id=%d) is not found", srcServicePriceListId));
		}

		if (srcPriceList.getPriceListLevel() != PRICE_LEVEL_NMC) {
			throw new UnsupportedOperationException();
		}

		ServicePriceList newPriceList = copyServicePriceList(srcPriceList);
		newPriceList.setSrcPriceListId(srcServicePriceListId);
		newPriceList.setPriceListLevel(PRICE_LEVEL_RMA);
		newPriceList.setSubscriberId1(subscriberId);
		newPriceList.setIsMaster(true);

		servicePriceListRepository.save(newPriceList);

		return newPriceList;
	}

	/**
	 * 
	 * @param srcServicePriceList
	 * @return
	 */
	private ServicePriceList copyServicePriceList(ServicePriceList srcServicePriceList) {
		checkNotNull(srcServicePriceList);
		checkArgument(!srcServicePriceList.isNew());
		ServicePriceList newPriceList = new ServicePriceList();
		newPriceList.setPriceListName(PRICE_LIST_PREFIX + srcServicePriceList.getPriceListName());
		newPriceList.setPriceListOption(srcServicePriceList.getPriceListOption());
		newPriceList.setPlanBeginDate(srcServicePriceList.getPlanBeginDate());
		newPriceList.setPlanEndDate(srcServicePriceList.getPlanEndDate());
		newPriceList.setIsActive(false);
		return newPriceList;
	}

}
