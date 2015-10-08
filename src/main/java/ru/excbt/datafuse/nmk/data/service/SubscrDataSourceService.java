package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.repository.SubscrDataSourceRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrDataSourceService implements SecuredRoles {

	@Autowired
	private SubscrDataSourceRepository subscrDataSourceRepository;

	@Autowired
	private DataSourceTypeRepository dataSourceTypeRepository;

	/**
	 * 
	 * @param subscrDataSource
	 */
	private void initSubscrDataSourceType(SubscrDataSource subscrDataSource) {
		checkNotNull(subscrDataSource);
		checkNotNull(subscrDataSource.getDataSourceTypeKey());
		DataSourceType dataSourceType = dataSourceTypeRepository.findOne(subscrDataSource.getDataSourceTypeKey());
		if (dataSourceType == null) {
			throw new PersistenceException(
					String.format("DataSourceType (id=%s) is not found", subscrDataSource.getDataSourceTypeKey()));
		}
		subscrDataSource.setDataSourceType(dataSourceType);
	}

	/**
	 * 
	 * @param subscrDataSource
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public SubscrDataSource createOne(SubscrDataSource subscrDataSource) {
		checkNotNull(subscrDataSource);
		checkArgument(subscrDataSource.isNew());
		checkNotNull(subscrDataSource.getDataSourceTypeKey());
		checkNotNull(subscrDataSource.getSubscriberId());

		initSubscrDataSourceType(subscrDataSource);

		return subscrDataSourceRepository.save(subscrDataSource);
	}

	/**
	 * 
	 * @param subscrDataSource
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public SubscrDataSource updateOne(SubscrDataSource subscrDataSource) {
		checkNotNull(subscrDataSource);
		checkArgument(!subscrDataSource.isNew());
		checkNotNull(subscrDataSource.getDataSourceTypeKey());
		checkNotNull(subscrDataSource.getSubscriberId());

		initSubscrDataSourceType(subscrDataSource);

		return subscrDataSourceRepository.save(subscrDataSource);
	}

	/**
	 * 
	 * @param dataSourceId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN })
	public void deleteOne(Long dataSourceId) {
		SubscrDataSource dataSource = subscrDataSourceRepository.findOne(dataSourceId);
		if (dataSource == null) {
			throw new PersistenceException(String.format("SubscrDataSource (id=%d) is not found", dataSourceId));
		}
		dataSource.setDeleted(1);
		updateOne(dataSource);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrDataSource> selectBySubscriber(Long subscriberId) {
		List<SubscrDataSource> list = subscrDataSourceRepository.findBySubscriberId(subscriberId);
		return ObjectFilters.deletedFilter(list);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrDataSource selectByKeyname(Long subscriberId, String keyname) {
		List<SubscrDataSource> list = subscrDataSourceRepository.findBySubscriberId(subscriberId);

		List<SubscrDataSource> result = ObjectFilters.deletedFilter(list.stream())
				.filter(i -> keyname.equals(i.getKeyname())).collect(Collectors.toList());

		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * 
	 * @param dataSourceId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrDataSource findOne(Long dataSourceId) {
		return subscrDataSourceRepository.findOne(dataSourceId);
	}
}
