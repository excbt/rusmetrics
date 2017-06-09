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
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_service_pack_special")
@Getter
@Setter
public class SubscrServicePackSpecial extends JsonAbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 5882132038839681219L;

	@Column(name = "subscr_service_pack_id")
	private Long subscrServicePackId;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
