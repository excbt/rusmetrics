package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_type")
@JsonInclude(Include.NON_NULL)
public class DeviceObjectPkeType extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1824978628446972924L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "pke_type_description")
	private String pkeTypeDescription;

	@Column(name = "pke_type_comment")
	private String pkeTypeComment;

	@Column(name = "pke_type_code")
	private String pkeTypeCode;

	@Column(name = "measure_unit")
	private String pkeMeasureUnit;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getPkeTypeDescription() {
		return pkeTypeDescription;
	}

	public void setPkeTypeDescription(String pkeTypeDescription) {
		this.pkeTypeDescription = pkeTypeDescription;
	}

	public String getPkeTypeComment() {
		return pkeTypeComment;
	}

	public void setPkeTypeComment(String pkeTypeComment) {
		this.pkeTypeComment = pkeTypeComment;
	}

	public String getPkeTypeCode() {
		return pkeTypeCode;
	}

	public void setPkeTypeCode(String pkeTypeCode) {
		this.pkeTypeCode = pkeTypeCode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getPkeMeasureUnit() {
		return pkeMeasureUnit;
	}

	public void setPkeMeasureUnit(String pkeMeasureUnit) {
		this.pkeMeasureUnit = pkeMeasureUnit;
	}

}
