package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.ExCodeObject;
import ru.excbt.datafuse.nmk.data.domain.ExLabelObject;
import ru.excbt.datafuse.nmk.data.domain.ExSystemObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "device_model")
public class DeviceModel extends AbstractAuditableModel implements
		ExSystemObject, ExCodeObject, ExLabelObject {

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

}
