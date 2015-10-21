package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "cont_service_type")
@JsonInclude(Include.NON_NULL)
public class ContServiceType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6558062018028025474L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "cont_service_type_name")
	private String name;

	@Column(name = "cont_service_type_comment")
	private String comment;

	@JsonIgnore
	@Column(name = "ex_code")
	private String exCode;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "service_order")
	private Integer serviceOrder;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Integer getServiceOrder() {
		return serviceOrder;
	}

	public void setServiceOrder(Integer serviceOrder) {
		this.serviceOrder = serviceOrder;
	}

}
