package ru.excbt.datafuse.nmk.data.model.keyname;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.RowAuditDate;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "time_detail_type")
public class TimeDetailType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "keyname")
	private String keyname;
		
	@Column(name = "caption")
	private String caption;

	@Column(name = "time_detail_type_name")
	private String name;

	@Column(name = "time_detail_type_comment")
	private String comment;
	
	@Version
	private int version;	
	
	@Embedded
	@JsonIgnore
	private RowAuditDate rowAudit;

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public RowAuditDate getRowAudit() {
		return rowAudit;
	}

	public void setRowAudit(RowAuditDate rowAudit) {
		this.rowAudit = rowAudit;
	}


	
}
