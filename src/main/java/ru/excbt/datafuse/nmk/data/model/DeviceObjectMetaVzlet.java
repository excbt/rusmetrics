package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Метаданные прибора для системы Взлет
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.05.2015
 *
 */
@Entity
@Table(name = "device_object_meta_vzlet")
public class DeviceObjectMetaVzlet extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2778200912535462611L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id", insertable = false, updatable = false)
	@JsonIgnore
	private DeviceObject deviceObject;

	@Column(name = "device_object_id")
	@JsonIgnore
	private Long deviceObjectId;

	@Column(name = "vzlet_table_hour")
	private String vzletTableHour;

	@Column(name = "vzlet_table_day")
	private String vzletTableDay;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id1", insertable = false, updatable = false)
	private VzletSystem vzletSystem1;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id2", insertable = false, updatable = false)
	private VzletSystem vzletSystem2;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id3", insertable = false, updatable = false)
	private VzletSystem vzletSystem3;

	@Column(name = "vzlet_system_id1")
	private Long vzletSystemId1;

	@Column(name = "vzlet_system_id2")
	private Long vzletSystemId2;

	@Column(name = "vzlet_system_id3")
	private Long vzletSystemId3;

	@Column(name = "exclude_nulls")
	@NotNull
	private Boolean excludeNulls;

	@Column(name = "meta_props_only")
	@NotNull
	private Boolean metaPropsOnly;

	@Version
	private int version;

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public String getVzletTableHour() {
		return vzletTableHour;
	}

	public void setVzletTableHour(String vzletTableHour) {
		this.vzletTableHour = vzletTableHour;
	}

	public String getVzletTableDay() {
		return vzletTableDay;
	}

	public void setVzletTableDay(String vzletTableDay) {
		this.vzletTableDay = vzletTableDay;
	}

	public VzletSystem getVzletSystem1() {
		return vzletSystem1;
	}

	public VzletSystem getVzletSystem2() {
		return vzletSystem2;
	}

	public VzletSystem getVzletSystem3() {
		return vzletSystem3;
	}

	public Long getVzletSystemId1() {
		return vzletSystemId1;
	}

	public void setVzletSystemId1(Long vzletSystemId1) {
		this.vzletSystemId1 = vzletSystemId1;
	}

	public Long getVzletSystemId2() {
		return vzletSystemId2;
	}

	public void setVzletSystemId2(Long vzletSystemId2) {
		this.vzletSystemId2 = vzletSystemId2;
	}

	public Long getVzletSystemId3() {
		return vzletSystemId3;
	}

	public void setVzletSystemId3(Long vzletSystemId3) {
		this.vzletSystemId3 = vzletSystemId3;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public void setDeviceObjectId(Long deviceObjectId) {
		this.deviceObjectId = deviceObjectId;
	}

	public Boolean getExcludeNulls() {
		return excludeNulls;
	}

	public void setExcludeNulls(Boolean excludeNulls) {
		this.excludeNulls = excludeNulls;
	}

	public Boolean getMetaPropsOnly() {
		return metaPropsOnly;
	}

	public void setMetaPropsOnly(Boolean metaPropsOnly) {
		this.metaPropsOnly = metaPropsOnly;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
