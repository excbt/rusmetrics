package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(name = "measure_unit")
@Getter
@Setter
public class MeasureUnit extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = -2220706708330783417L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "measure_unit_name")
	private String unitName;

	@Column(name = "measure_unit_comment")
	private String unitComment;

	@Column(name = "measure_unit_description")
	private String unitDescription;

	@Column(name = "measure_category")
	private String measureCategory;

	@Version
	@JsonIgnore
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
