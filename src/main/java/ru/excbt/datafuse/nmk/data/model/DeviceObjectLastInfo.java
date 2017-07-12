package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_last_info")
@Getter
@Setter
public class DeviceObjectLastInfo extends JsonAbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -5353859385235208349L;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id", insertable = false, updatable = false)
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "device_last_time", insertable = false, updatable = false)
	private Date deviceLastTime;

	@Column(name = "driver_last_time", insertable = false, updatable = false)
	private Date driverLastTime;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;


	public String getDeviceLastTimeStr() {
		return deviceLastTime == null ? null
				: DateFormatUtils.formatDateTime(deviceLastTime, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

	public String getDriverLastTimeStr() {
		return driverLastTime == null ? null
				: DateFormatUtils.formatDateTime(driverLastTime, DateFormatUtils.DATE_FORMAT_STR_FULL_SEC);
	}

}
