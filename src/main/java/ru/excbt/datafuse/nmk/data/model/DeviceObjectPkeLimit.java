package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_limit")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class DeviceObjectPkeLimit extends AbstractAuditableModel {

	/**
		 *
		 */
	private static final long serialVersionUID = 2648198576769118698L;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "device_object_pke_type")
	private String deviceObjectPkeType;

	@Column(name = "limit_date")
	private Date limitDate;

	@Column(name = "limit_value", columnDefinition = "numeric")
	private Double limitValue;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
