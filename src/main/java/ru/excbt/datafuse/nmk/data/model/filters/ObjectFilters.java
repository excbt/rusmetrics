package ru.excbt.datafuse.nmk.data.model.filters;

import static org.junit.Assert.assertNotNull;

import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

public class ObjectFilters {
	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DisabledObject> Stream<T> disabledFilter(
			Stream<T> inStream, boolean value) {
		assertNotNull(inStream);
		Boolean check = !Boolean.valueOf(value);
		return inStream.filter((i) -> !check.equals(i.getIsDisabled()));
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DisabledObject> Stream<T> disabledFilter(
			Stream<T> inStream) {
		return disabledFilter(inStream, false);
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DevModeObject> Stream<T> devModeFilter(
			Stream<T> inStream, boolean value) {
		assertNotNull(inStream);
		Boolean check = !Boolean.valueOf(value);
		return inStream.filter((i) -> !check.equals(i.getIsDevMode()));
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DevModeObject> Stream<T> devModeFilter(
			Stream<T> inStream) {
		return devModeFilter(inStream, false);
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DeletableObject> Stream<T> deletedFilter(
			Stream<T> inStream) {
		assertNotNull(inStream);
		return inStream.filter((i) -> i.getDeleted() == 0);
	}

}
