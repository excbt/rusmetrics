package ru.excbt.datafuse.nmk.data.model.support;

import com.google.common.base.MoreObjects;

public class MetaFieldValue {

	private final String fieldName;
	private final Object fieldValue;
	private final MetaFieldType fieldType;

	public MetaFieldValue(String fieldName, Object fieldValue,
			MetaFieldType fieldType) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public MetaFieldType getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("fieldName", fieldName)
				.add("fieldValue", fieldValue).add("fieldType", fieldType)
				.toString();
	}

}
