package ru.excbt.datafuse.nmk.data.model;

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
 * Настройка времени загрузки данных с прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.01.2016
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "device_object_loading_settings")
@Getter
@Setter
public class DeviceObjectLoadingSettings extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -4737222596632225231L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	private Long deviceObjectId;

	@Column(name = "is_loading_auto")
	private Boolean isLoadingAuto;

	@Column(name = "loading_interval")
	private String loadingInterval;

	@Column(name = "loading_attempts")
	private Integer loadingAttempts;

	@Column(name = "loading_retry_interval")
	private String loadingRetryInterval;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted", updatable = false)
	private int deleted;

}
