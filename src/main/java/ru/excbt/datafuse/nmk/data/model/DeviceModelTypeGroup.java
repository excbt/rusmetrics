/**
 *
 */
package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.10.2016
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_type_group")
public class DeviceModelTypeGroup extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4372750344328506338L;

	@Column(name = "device_model_id")
    @Getter
    @Setter
	private Long deviceModelId;

	@Column(name = "device_model_type")
    @Getter
    @Setter
	private String deviceModelType;

	@Version
    @Getter
    @Setter
	private int version;

	@Column(name = "deleted")
    @Getter
    @Setter
	private int deleted;

}
