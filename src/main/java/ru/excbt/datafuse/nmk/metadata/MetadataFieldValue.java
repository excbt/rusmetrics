package ru.excbt.datafuse.nmk.metadata;

import com.google.common.base.MoreObjects;

public class MetadataFieldValue {

	private final String srcProp;
	private final String destProp;
	private final Object fieldValue;
	private final MetadataFieldType fieldType;

	public MetadataFieldValue(String srcProp, String destProp, Object fieldValue,
			MetadataFieldType fieldType) {
		this.srcProp = srcProp;
		this.destProp = destProp;
		this.fieldValue = fieldValue;
		this.fieldType = fieldType;
	}


	public Object getFieldValue() {
		return fieldValue;
	}

	public MetadataFieldType getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("srcProp", srcProp).add("destProp", destProp)
				.add("fieldValue", fieldValue).add("fieldType", fieldType)
				.toString();
	}


	public String getSrcProp() {
		return srcProp;
	}


	public String getDestProp() {
		return destProp;
	}

}
