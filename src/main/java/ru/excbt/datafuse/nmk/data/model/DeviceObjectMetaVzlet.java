package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name = "device_object_meta_vzlet")
public class DeviceObjectMetaVzlet extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2778200912535462611L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")	
	private DeviceObject deviceObject;
	
	@Column (name = "vzlet_table_hour")
	private String vzletTableHour;
	
	@Column (name = "vzlet_table_day")
	private String vzletTableDay;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id1")	
	private VzletSystem vzletSystem1;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id2")	
	private VzletSystem vzletSystem2;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id3")	
	private VzletSystem vzletSystem3;

	public DeviceObject getDeviceObject() {
		return deviceObject;
	}

	public void setDeviceObject(DeviceObject deviceObject) {
		this.deviceObject = deviceObject;
	}

	public String getVzletTableHour() {
		return vzletTableHour;
	}

	public void setVzletTableHour(String vzletTableHour) {
		this.vzletTableHour = vzletTableHour;
	}

	public String getVzletTableDay() {
		return vzletTableDay;
	}

	public void setVzletTableDay(String vzletTableDay) {
		this.vzletTableDay = vzletTableDay;
	}

	public VzletSystem getVzletSystem1() {
		return vzletSystem1;
	}

	public void setVzletSystem1(VzletSystem vzletSystem1) {
		this.vzletSystem1 = vzletSystem1;
	}

	public VzletSystem getVzletSystem2() {
		return vzletSystem2;
	}

	public void setVzletSystem2(VzletSystem vzletSystem2) {
		this.vzletSystem2 = vzletSystem2;
	}

	public VzletSystem getVzletSystem3() {
		return vzletSystem3;
	}

	public void setVzletSystem3(VzletSystem vzletSystem3) {
		this.vzletSystem3 = vzletSystem3;
	}
}
