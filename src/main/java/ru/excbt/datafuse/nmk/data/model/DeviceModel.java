package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;


@Entity
@Table(name="device_model")
@EntityListeners({AuditingEntityListener.class})
public class DeviceModel extends AbstractAuditableEntity<AuditUser, Long>{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6370569022830583056L;

	
	@Column(name="device_model_name")
	private String modelName;

	@Column(name="keyname")
	private String keyname;

	@Column(name="caption")
	private String caption;

	@Column(name="ex_code")
	private String exCode;
	
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
	
}
