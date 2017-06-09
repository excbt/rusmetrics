package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "cont_service_type")
@JsonInclude(Include.NON_NULL)
@Getter
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

	@Column(name = "service_order")
	private Integer serviceOrder;

	@Column(name = "caption_short")
	private String captionShort;

}
