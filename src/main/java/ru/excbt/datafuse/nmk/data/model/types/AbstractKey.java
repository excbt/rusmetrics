package ru.excbt.datafuse.nmk.data.model.types;

import java.util.EnumSet;
import java.util.Set;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public interface AbstractKey extends KeynameObject {

	/**
	 * 
	 * @param enumClass
	 * @param keyname
	 * @return
	 */
	public static <E extends Enum<E> & KeynameObject> E getEnumCodeFull(Class<E> enumClass, String keyname) {

		Set<E> setE = EnumSet.allOf(enumClass);
		for (E e : setE) {
			if (e.getKeyname().equals(keyname)) {
				return e;
			}
		}
		return null;
	}

}
