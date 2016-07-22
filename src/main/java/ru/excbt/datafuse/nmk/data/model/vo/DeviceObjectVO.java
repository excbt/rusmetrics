package ru.excbt.datafuse.nmk.data.model.vo;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.V_DeviceObjectTimeOffset;
import ru.excbt.datafuse.nmk.data.model.support.ModelWrapper;

public class DeviceObjectVO extends ModelWrapper<DeviceObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7805901798419117132L;

	private V_DeviceObjectTimeOffset deviceObjectTimeOffset;

	public DeviceObjectVO(DeviceObject srcObject) {
		super(srcObject);

	}

	public V_DeviceObjectTimeOffset getDeviceObjectTimeOffset() {
		return deviceObjectTimeOffset;
	}

	public void setDeviceObjectTimeOffset(V_DeviceObjectTimeOffset deviceObjectTimeOffset) {
		this.deviceObjectTimeOffset = deviceObjectTimeOffset;
	}

}
