package ru.excbt.datafuse.nmk.data.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.factory.Mappers;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.service.mapper.SubscrDataSourceMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditDataSourceDTO {

    private Long id;

	private Long subscrDataSourceId;

	private String subscrDataSourceAddr;

	private String dataSourceTable;

	private String dataSourceTable1h;

	private String dataSourceTable24h;

	private String dataSourceName;

	public EditDataSourceDTO() {

	}

	public EditDataSourceDTO(DeviceObjectDataSource deviceObjectDataSource) {
		this.id = deviceObjectDataSource.getId();
		this.subscrDataSourceId = deviceObjectDataSource.getSubscrDataSourceId();
		this.subscrDataSourceAddr = deviceObjectDataSource.getSubscrDataSourceAddr();
		this.dataSourceTable = deviceObjectDataSource.getDataSourceTable();
		this.dataSourceTable1h = deviceObjectDataSource.getDataSourceTable1h();
		this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceTable24h = deviceObjectDataSource.getDataSourceTable24h();
        this.dataSourceName = deviceObjectDataSource.getSubscrDataSource().getDataSourceName();
	}

}
