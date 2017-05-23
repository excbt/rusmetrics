package ru.excbt.datafuse.nmk.data.model.dmo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;

@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ActiveDataSourceInfoDMO {

	private Long id;
	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	public ActiveDataSourceInfoDMO() {

	}

	public ActiveDataSourceInfoDMO(DeviceObjectDataSource deviceObjectDataSource) {
		this.subscrDataSourceId = deviceObjectDataSource.getId();
		this.id = deviceObjectDataSource.getId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
	}

}
