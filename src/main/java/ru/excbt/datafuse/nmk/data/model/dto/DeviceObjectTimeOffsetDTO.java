package ru.excbt.datafuse.nmk.data.model.dto;

import java.util.Date;

public class DeviceObjectTimeOffsetDTO {

    private Long deviceObjectId;

    private Date deviceLastTime;

    private Date driverLastTime;

    private Integer timeDeltaSign;

    private Integer years;

    private Integer mons;

    private Integer days;

    private Integer hh;

    private Integer mm;

    private Integer ss;

    public Long getDeviceObjectId() {
        return deviceObjectId;
    }

    public void setDeviceObjectId(Long deviceObjectId) {
        this.deviceObjectId = deviceObjectId;
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
}
