package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.factory.Mappers;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource2;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.service.mapper.SubscrDataSourceMapper;

import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveDataSourceInfoDTO {

    private Long id;

	private Long subscrDataSourceId;

	private SubscrDataSourceDTO subscrDataSource;

	private String subscrDataSourceAddr;

	private String dataSourceTable;

	private String dataSourceTable1h;

	private String dataSourceTable24h;

	private String dataSourceName;

	public ActiveDataSourceInfoDTO() {

	}

	public ActiveDataSourceInfoDTO(DeviceObjectDataSource deviceObjectDataSource) {
		this.id = deviceObjectDataSource.getId();
		this.subscrDataSourceId = deviceObjectDataSource.getSubscrDataSourceId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceName = deviceObjectDataSource.getSubscrDataSource().getDataSourceName();
	}

	public ActiveDataSourceInfoDTO(DeviceObjectDataSource2 deviceObjectDataSource) {
        Objects.requireNonNull(deviceObjectDataSource);
		//this.id = deviceObjectDataSource.getId();
		this.subscrDataSourceId = Optional.of(deviceObjectDataSource)
            .map(DeviceObjectDataSource2::getSubscrDataSource).map(SubscrDataSource::getId).orElse(null);
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceName = deviceObjectDataSource.getSubscrDataSource().getDataSourceName();
	}

}
