package ru.excbt.datafuse.nmk.data.constant;

public enum ParamType {
	NUMBER("Number"), BOOLEAN("Boolean"), STRING("String");

	private final String camelName;

	private ParamType(String arg) {
		this.camelName = arg;
	}

	public String camelName() {
		return this.camelName;
	}

	public String getKeyname() {
		return this.name();
	}

}
