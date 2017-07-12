package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "device_metadata_type")
@Getter
public class DeviceMetadataType extends AbstractKeynameEntity implements DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 2899139629392944981L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "meta_type_name")
	private String typeName;

	@Column(name = "meta_type_comment")
	private String typeComment;

	@Column(name = "meta_type_description")
	private String typeDescription;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

}
