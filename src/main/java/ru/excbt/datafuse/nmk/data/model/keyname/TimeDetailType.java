package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(name = "time_detail_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class TimeDetailType extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = 3417410297560210311L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "time_detail_type_name")
	private String name;

	@Column(name = "time_detail_type_comment")
	private String comment;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
