package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="cont_event_type")
public class ContEventType extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;
	
	@Column(name = "cont_event_type_name")
	private String name; 

	@Column(name = "cont_event_type_comment")
	private String comment;
	
	@Embedded
	@JsonIgnore
	private RowAudit rowAudit;	
	
}
