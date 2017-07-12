package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Subselect;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Subselect("select * from portal.v_device_object_time_offset")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class V_DeviceObjectTimeOffset implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8234898997252596688L;

	@Id
	@Column(name = "device_object_id")
	private Long deviceObjectId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "device_last_time")
	private Date deviceLastTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "driver_last_time")
	private Date driverLastTime;

	@Column(name = "time_delta_sign")
	private Integer timeDeltaSign;

	@Column(name = "years")
	private Integer years;

	@Column(name = "mons")
	private Integer mons;

	@Column(name = "days")
	private Integer days;

	@Column(name = "hh")
	private Integer hh;

	@Column(name = "mm")
	private Integer mm;

	@Column(name = "ss")
	private Integer ss;

}
