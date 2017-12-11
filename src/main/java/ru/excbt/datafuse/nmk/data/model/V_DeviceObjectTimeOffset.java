package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Subselect;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Subselect("select * from portal.v_device_object_time_offset")
@Cache(usage = CacheConcurrencyStrategy.NONE)
//@Getter
//@Setter
//@IdClass(DeviceObject.class)
public class V_DeviceObjectTimeOffset implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8234898997252596688L;

//	@JsonIgnore
	@Id
    @OneToOne
    @JoinColumn(name = "device_object_id", updatable = false, insertable = false)
	private DeviceObject deviceObject;

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


	@JsonIgnore
    public DeviceObject getDeviceObject() {
        return deviceObject;
    }

    public void setDeviceObject(DeviceObject deviceObject) {
        this.deviceObject = deviceObject;
    }

    public Date getDeviceLastTime() {
        return deviceLastTime;
    }

    public void setDeviceLastTime(Date deviceLastTime) {
        this.deviceLastTime = deviceLastTime;
    }

    public Date getDriverLastTime() {
        return driverLastTime;
    }

    public void setDriverLastTime(Date driverLastTime) {
        this.driverLastTime = driverLastTime;
    }

    public Integer getTimeDeltaSign() {
        return timeDeltaSign;
    }

    public void setTimeDeltaSign(Integer timeDeltaSign) {
        this.timeDeltaSign = timeDeltaSign;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getMons() {
        return mons;
    }

    public void setMons(Integer mons) {
        this.mons = mons;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getHh() {
        return hh;
    }

    public void setHh(Integer hh) {
        this.hh = hh;
    }

    public Integer getMm() {
        return mm;
    }

    public void setMm(Integer mm) {
        this.mm = mm;
    }

    public Integer getSs() {
        return ss;
    }

    public void setSs(Integer ss) {
        this.ss = ss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        V_DeviceObjectTimeOffset that = (V_DeviceObjectTimeOffset) o;
        return Objects.equals(deviceObject, that.deviceObject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(deviceObject);
    }
}
