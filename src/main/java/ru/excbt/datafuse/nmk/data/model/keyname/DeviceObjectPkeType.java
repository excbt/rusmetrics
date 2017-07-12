package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_type")
@JsonInclude(Include.NON_NULL)
@Getter
public class DeviceObjectPkeType extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = -1824978628446972924L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "pke_type_description")
	private String pkeTypeDescription;

	@Column(name = "pke_type_comment")
	private String pkeTypeComment;

	@Column(name = "pke_type_code")
	private String pkeTypeCode;

	@Column(name = "measure_unit")
	private String measureUnit;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
