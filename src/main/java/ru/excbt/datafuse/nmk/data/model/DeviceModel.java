package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

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

@Entity
@Table(name = "device_model")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
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

}
