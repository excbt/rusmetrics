/**
 *
 */
package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_building_type_category")
@Getter
public class BuildingTypeCategory extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = -3879053027088782119L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@Column(name = "building_type")
	private String buildingType;

	@Column(name = "parent_category")
	private String parentCategory;

	@Column(name = "order_idx")
	private Integer orderIdx;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
