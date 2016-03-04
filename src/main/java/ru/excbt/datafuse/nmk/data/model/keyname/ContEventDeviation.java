package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_deviation")
@JsonInclude(Include.NON_NULL)
public class ContEventDeviation extends AbstractKeynameEntity implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2972653600934364021L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "deviation_name")
	private String deviationName;

	@Column(name = "deviation_description")
	private String deviationDescription;

	@JsonIgnore
	@Column(name = "deviation_comment")
	private String deviationComment;

	@Column(name = "deviation_order")
	private Integer deviationOrder;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDeviationName() {
		return deviationName;
	}

	public void setDeviationName(String deviationName) {
		this.deviationName = deviationName;
	}

	public String getDeviationDescription() {
		return deviationDescription;
	}

	public void setDeviationDescription(String deviationDescription) {
		this.deviationDescription = deviationDescription;
	}

	public String getDeviationComment() {
		return deviationComment;
	}

	public void setDeviationComment(String deviationComment) {
		this.deviationComment = deviationComment;
	}

	public Integer getDeviationOrder() {
		return deviationOrder;
	}

	public void setDeviationOrder(Integer deviationOrder) {
		this.deviationOrder = deviationOrder;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}