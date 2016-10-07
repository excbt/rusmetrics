/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.10.2016
 * 
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_device_model_type")
public class DeviceModelType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1077699614235498732L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@JsonIgnore
	@Column(name = "description")
	private String description;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "order_idx")
	private Integer orderIdx;

	@Override
	public String getKeyname() {
		return keyname;
	}

	@Override
	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}