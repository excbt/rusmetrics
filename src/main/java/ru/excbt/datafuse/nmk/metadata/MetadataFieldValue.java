package ru.excbt.datafuse.nmk.metadata;

import com.google.common.base.MoreObjects;

public class MetadataFieldValue {

	private final String srcProp;
	private final String destProp;
	private final Object fieldValue;
	private final String destDbType;

	public MetadataFieldValue(String srcProp, String destProp,
			Object fieldValue, String destDbType) {
		this.srcProp = srcProp;
		this.destProp = destProp;
		this.fieldValue = fieldValue;
		this.destDbType = destDbType;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("srcProp", srcProp)
				.add("destProp", destProp).add("fieldValue", fieldValue)
				.add("destDbType", destDbType).toString();
	}

	public String getSrcProp() {
		return srcProp;
	}

	public String getDestProp() {
		return destProp;
	}

	public String getDestDbType() {
		return destDbType;
	}

}
