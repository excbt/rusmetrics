package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum DeviceMetadataTypeKeyname implements KeynameObject {
	DEVICE, DEVICE_RAW, VZL;

	@Override
	public String getKeyname() {
		return this.name();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static DeviceMetadataTypeKeyname searchKeyname(String keyname) {

		Optional<DeviceMetadataTypeKeyname> opt = Stream.of(DeviceMetadataTypeKeyname.values())
				.filter((i) -> i.getKeyname().equals(keyname)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

}
