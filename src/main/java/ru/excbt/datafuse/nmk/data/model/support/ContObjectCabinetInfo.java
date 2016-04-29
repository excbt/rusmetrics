package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import ru.excbt.datafuse.nmk.data.model.ContObject;

public class ContObjectCabinetInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2605764638950997718L;

	private final ContObjectShortInfo contObjectInfo;

	@JsonProperty(value = "cabinet")
	private final SubscrCabinetInfo subscrCabinetInfo;

	/**
	 * 
	 * @param contObject
	 * @param subscrCabinetInfo
	 */
	public ContObjectCabinetInfo(ContObject contObject, SubscrCabinetInfo subscrCabinetInfo) {
		this.contObjectInfo = contObject.getContObjectShortInfo();
		this.subscrCabinetInfo = subscrCabinetInfo;
	}

	/**
	 * 
	 * @return
	 */
	public ContObjectShortInfo getContObjectInfo() {
		return contObjectInfo;
	}

	/**
	 * 
	 * @return
	 */
	public SubscrCabinetInfo getSubscrCabinetInfo() {
		return subscrCabinetInfo;
	}

}
