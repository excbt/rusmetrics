package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSourceLoadingSettings;
import ru.excbt.datafuse.nmk.data.repository.SubscrDataSourceLoadingSettingsRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrDataSourceLoadingSettingsService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDataSourceLoadingSettingsService.class);

	@Autowired
	private SubscrDataSourceLoadingSettingsRepository subscrDataSourceLoadingSettingsRepository;

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrDataSourceLoadingSettings findSubscrDataSourceLoadingSettings(Long subscrDataSourceId) {
		SubscrDataSourceLoadingSettings result = subscrDataSourceLoadingSettingsRepository.findOne(subscrDataSourceId);
		if (result.getSubscrDataSource() != null) {
			result.setDataSourceName(result.getSubscrDataSource().getDataSourceName());
		}
		return result;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional
	public SubscrDataSourceLoadingSettings saveSubscrDataSourceLoadingSettings(SubscrDataSourceLoadingSettings entity) {
		checkNotNull(entity.getSubscrDataSource());
		checkArgument(!entity.getSubscrDataSource().isNew());
		if (!entity.isNew()) {
			checkArgument(entity.getSubscrDataSource().getId().equals(entity.getSubscrDataSourceId()));
		}
		entity.setSubscrDataSourceId(entity.getSubscrDataSource().getId());
		return subscrDataSourceLoadingSettingsRepository.save(entity);
	}

	/**
	 *
	 * @param subscrDataSource
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrDataSourceLoadingSettings getSubscrDataSourceLoadingSettings(SubscrDataSource subscrDataSource) {
		SubscrDataSourceLoadingSettings result = null;
		if (subscrDataSource.isNew()) {
			result = new SubscrDataSourceLoadingSettings();
			result.setSubscrDataSource(subscrDataSource);
			result.setSubscrDataSourceId(subscrDataSource.getId());
			result.setDataSourceName(subscrDataSource.getDataSourceName());
			return result;
		}

		List<SubscrDataSourceLoadingSettings> resultList = subscrDataSourceLoadingSettingsRepository
				.findBySubscrDataSourceId(subscrDataSource.getId());

		if (resultList.isEmpty()) {
			result = new SubscrDataSourceLoadingSettings();
			result.setSubscrDataSource(subscrDataSource);
			result.setSubscrDataSourceId(subscrDataSource.getId());
			result.setDataSourceName(subscrDataSource.getDataSourceName());
			return result;
		}

		result = resultList.get(0);
		result.setDataSourceName(subscrDataSource.getDataSourceName());

		return result;
	}

	/**
	 *
	 * @param subscrDataSource
	 * @return
	 */
	public SubscrDataSourceLoadingSettings newSubscrDataSourceLoadingSettings(SubscrDataSource subscrDataSource) {
		checkNotNull(subscrDataSource);
		SubscrDataSourceLoadingSettings result = new SubscrDataSourceLoadingSettings();
		result.setSubscrDataSource(subscrDataSource);
		result.setSubscrDataSourceId(subscrDataSource.getId());
		result.setIsLoadingAuto(true);
		result.setLoadingAttempts(1);
		result.setLoadingInterval("01:00");
		result.setDataSourceName(subscrDataSource.getDataSourceName());
		return result;
	}

}
