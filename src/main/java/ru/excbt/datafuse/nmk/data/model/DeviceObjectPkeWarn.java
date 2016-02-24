package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_warn")
@JsonInclude(Include.NON_NULL)
public class DeviceObjectPkeWarn extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4032736746402754373L;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "device_object_pke_type")
	private String deviceObjectPkeType;

	@Column(name = "warn_start_date")
	private Date warnStartDate;

	@Column(name = "warn_end_date")
	private Date warnEndDate;

	@Column(name = "warn_value")
	private BigDecimal warnValue;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
