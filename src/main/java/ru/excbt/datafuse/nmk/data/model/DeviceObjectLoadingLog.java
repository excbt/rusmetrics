package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Данные о последней загрузки данных с прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.01.2016
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "device_object_loading_log")
@Getter
@Setter
public class DeviceObjectLoadingLog extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 7350434737316386194L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "last_loading_time")
	private Date lastLoadingTime;

	@Column(name = "last_attempt_time")
	private Date lastAttemptTime;

	@Column(name = "retry_attempts")
	private Integer retryAttempts;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted", updatable = false)
	private int deleted;

}
