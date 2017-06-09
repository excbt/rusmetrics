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
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(name = "cont_event_category")
@JsonInclude(Include.NON_NULL)
@Getter
public class ContEventCategory extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = 667344996165856933L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "category_description")
	private String categoryDescription;

	@JsonIgnore
	@Column(name = "category_comment")
	private String categoryComment;

	@Column(name = "deleted")
	private int deleted;

	@Version
	private int version;

	@Column(name = "category_order")
	private Integer categoryOrder;

}
