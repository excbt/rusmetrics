package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class DataSourceInfoDTO {

	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	private String dataSourceTable;

	private String dataSourceTable1h;

	private String dataSourceTable24h;

	public DataSourceInfoDTO() {

	}

	public DataSourceInfoDTO(DeviceObjectDataSource deviceObjectDataSource) {
		this.subscrDataSourceId = deviceObjectDataSource.getId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
	}

}
