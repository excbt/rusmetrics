package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_deviation")
@JsonInclude(Include.NON_NULL)
@Getter
public class ContEventDeviation extends AbstractKeynameEntity implements DeletedMarker {

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

}
