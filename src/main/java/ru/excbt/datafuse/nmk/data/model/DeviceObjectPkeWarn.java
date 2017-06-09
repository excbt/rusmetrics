package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_pke_warn")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class DeviceObjectPkeWarn extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4032736746402754373L;

	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Column(name = "device_object_pke_type")
	private String deviceObjectPkeTypeKeyname;

	@Column(name = "warn_start_date")
	private Date warnStartDate;

	@Column(name = "warn_end_date")
	private Date warnEndDate;

	@Column(name = "warn_value")
	private BigDecimal warnValue;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
