package ru.excbt.datafuse.nmk.data.model.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceInfo {

	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	private String dataSourceTable;

	private String dataSourceTable1h;

	private String dataSourceTable24h;

	public DataSourceInfo() {

	}

	public DataSourceInfo(DeviceObjectDataSource deviceObjectDataSource) {
		this.subscrDataSourceId = deviceObjectDataSource.getId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
	}

	public Long getSubscrDataSourceId() {
		return subscrDataSourceId;
	}

	public void setSubscrDataSourceId(Long subscrDataSourceId) {
		this.subscrDataSourceId = subscrDataSourceId;
	}

	public String getSubscrDataSourceAddr() {
		return subscrDataSourceAddr;
	}

	public void setSubscrDataSourceAddr(String subscrDataSourceAddr) {
		this.subscrDataSourceAddr = subscrDataSourceAddr;
	}

	public String getDataSourceTable() {
		return dataSourceTable;
	}

	public void setDataSourceTable(String dataSourceTable) {
		this.dataSourceTable = dataSourceTable;
	}

	public String getDataSourceTable1h() {
		return dataSourceTable1h;
	}

	public void setDataSourceTable1h(String dataSourceTable1h) {
		this.dataSourceTable1h = dataSourceTable1h;
	}

	public String getDataSourceTable24h() {
		return dataSourceTable24h;
	}

	public void setDataSourceTable24h(String dataSourceTable24h) {
		this.dataSourceTable24h = dataSourceTable24h;
	}
}
