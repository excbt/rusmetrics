package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "data_source_type")
@JsonInclude(Include.NON_NULL)
public class DataSourceType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7406211994555671115L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "is_raw")
	private Boolean isRaw;

	@Column(name = "is_db")
	private Boolean isDB;

	@Column(name = "type_name")
	private String typeName;

	@Column(name = "type_description")
	private String typeDescription;

	@Column(name = "type_comment")
	private String typeComment;

	@Column(name = "type_order")
	private Integer typeOrder;

	public String getCaption() {
		return caption;
	}

	public Boolean getIsRaw() {
		return isRaw;
	}

	public Boolean getIsDB() {
		return isDB;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public String getTypeComment() {
		return typeComment;
	}

	public Integer getTypeOrder() {
		return typeOrder;
	}

}
