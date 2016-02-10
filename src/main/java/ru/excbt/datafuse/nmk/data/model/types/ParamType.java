package ru.excbt.datafuse.nmk.data.model.types;

/**
 * Тип данных: типы параметров
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
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
