package ru.excbt.datafuse.nmk.data.model.dmo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class DataSourceInfoDMO {

	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	public DataSourceInfoDMO() {

	}

	public DataSourceInfoDMO(DeviceObjectDataSource deviceObjectDataSource) {
		this.subscrDataSourceId = deviceObjectDataSource.getId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
	}

}
