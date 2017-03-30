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
public class ActiveDataSourceInfoDTO {

    private Long id;

	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	private String dataSourceTable;

	private String dataSourceTable1h;

	private String dataSourceTable24h;

	public ActiveDataSourceInfoDTO() {

	}

	public ActiveDataSourceInfoDTO(DeviceObjectDataSource deviceObjectDataSource) {
		this.id = deviceObjectDataSource.getId();
		this.subscrDataSourceId = deviceObjectDataSource.getId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
	}

}
