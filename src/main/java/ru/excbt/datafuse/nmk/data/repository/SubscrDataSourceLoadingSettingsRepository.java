package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrDataSourceLoadingSettings;

public interface SubscrDataSourceLoadingSettingsRepository
		extends CrudRepository<SubscrDataSourceLoadingSettings, Long> {

	public List<SubscrDataSourceLoadingSettings> findBySubscrDataSourceId(Long subscrDataSourceId);

}
