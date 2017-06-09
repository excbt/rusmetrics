package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExLabelObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;

/**
 * Модель прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2015
 *
 */
@Entity
@Table(name = "device_model")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class DeviceModel extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, ExLabelObject, DevModeObject, DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -6370569022830583056L;

	@Column(name = "device_model_name")
	private String modelName;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ex_label")
	private String exLabel;

	@Column(name = "ex_system", insertable = false, updatable = false)
	private String exSystem;

	@Column(name = "ex_system")
	@JsonIgnore
	private String exSystemKeyname;

	@Version
	private int version;

	@Column(name = "is_dev_mode")
	private Boolean isDevMode;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "meta_version")
	private Integer metaVersion = 1;

	@Column(name = "is_impulse")
	private Boolean isImpulse;

	@Column(name = "default_impulse_k")
	private BigDecimal defaultImpulseK;

	@Column(name = "default_impulse_mu")
	private String defaultImpulseMu;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_type_group",
			joinColumns = @JoinColumn(name = "device_model_id"))
	@Column(name = "device_model_type")
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<String> deviceModelTypes = new HashSet<>();

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	@Override
	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String getExLabel() {
		return exLabel;
	}

	public void setExLabel(String exLabel) {
		this.exLabel = exLabel;
	}

	public String getExSystem() {
		return exSystem;
	}

	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}

	@Override
	public String getExSystemKeyname() {
		return exSystemKeyname;
	}

	public void setExSystemKeyname(String exSystemKeyname) {
		this.exSystemKeyname = exSystemKeyname;
	}

	@Override
	public Boolean getIsDevMode() {
		return isDevMode;
	}

	public void setIsDevMode(Boolean isDevMode) {
		this.isDevMode = isDevMode;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Integer getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(Integer metaVersion) {
		this.metaVersion = metaVersion;
	}

	public Boolean getIsImpulse() {
		return isImpulse;
	}

	public void setIsImpulse(Boolean isImpulse) {
		this.isImpulse = isImpulse;
	}

	public BigDecimal getDefaultImpulseK() {
		return defaultImpulseK;
	}

	public void setDefaultImpulseK(BigDecimal defaultImpulseK) {
		this.defaultImpulseK = defaultImpulseK;
	}

	public String getDefaultImpulseMu() {
		return defaultImpulseMu;
	}

	public void setDefaultImpulseMu(String defaultImpulseMu) {
		this.defaultImpulseMu = defaultImpulseMu;
	}

	public Set<String> getDeviceModelTypes() {
		return deviceModelTypes;
	}

	public void setDeviceModelTypes(Set<String> deviceModelTypes) {
		this.deviceModelTypes = deviceModelTypes;
	}

}
