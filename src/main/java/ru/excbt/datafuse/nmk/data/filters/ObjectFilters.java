package ru.excbt.datafuse.nmk.data.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

/**
 * Набор фильтров для работы с сущностями системы
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.09.2015
 *
 */
public class ObjectFilters {

	public static final Predicate<? super DeletedMarker> NO_DELETED_OBJECT_PREDICATE = (i) -> i.getDeleted() == 0;
	public static final Predicate<? super ActiveObject> ACTIVE_OBJECT_PREDICATE = (i) -> Boolean.TRUE
			.equals(i.getIsActive());
	public static final Predicate<? super DevModeObject> NO_DEV_MODE_OBJECT_PREDICATE = (
			i) -> !Boolean.TRUE.equals(i.getIsDevMode());

	public static final Predicate<? super DisabledObject> NO_DISABLED_OBJECT_PREDICATE = (
			i) -> !Boolean.TRUE.equals(i.getIsDisabled());

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DisabledObject> Stream<T> disabledFilter(Stream<T> inStream, boolean value) {
		checkNotNull(inStream);
		Boolean check = !Boolean.valueOf(value);
		return inStream.filter((i) -> !check.equals(i.getIsDisabled()));
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DisabledObject> Stream<T> disabledFilter(Stream<T> inStream) {
		return disabledFilter(inStream, false);
	}

	/**
	 * 
	 * @param inList
	 * @return
	 */
	public static <T extends DisabledObject> List<T> disabledFilter(List<T> inList) {
		checkNotNull(inList);
		return disabledFilter(inList.stream(), false).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DevModeObject> Stream<T> devModeFilter(Stream<T> inStream, boolean value) {
		checkNotNull(inStream);
		Boolean check = !Boolean.valueOf(value);
		return inStream.filter((i) -> !check.equals(i.getIsDevMode()));
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DevModeObject> Stream<T> devModeFilter(Stream<T> inStream) {
		return devModeFilter(inStream, false);
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DevModeObject> List<T> devModeFilter(List<T> inStream) {
		return devModeFilter(inStream.stream(), false).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DeletedMarker> Stream<T> deletedFilter(Stream<T> inStream) {
		checkNotNull(inStream);
		return inStream.filter(NO_DELETED_OBJECT_PREDICATE);
	}

	/**
	 * 
	 * @param inObject
	 * @return
	 */
	public static <T extends DeletedMarker> T deletedFilter(T inObject) {
		if (inObject == null) {
			return null;
		}
		return NO_DELETED_OBJECT_PREDICATE.test(inObject) ? inObject : null;
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends DeletedMarker> List<T> deletedFilter(List<T> inList) {
		checkNotNull(inList);
		return filterToList(inList, NO_DELETED_OBJECT_PREDICATE);
	}

	/**
	 * 
	 * @param inStream
	 * @return
	 */
	public static <T extends ActiveObject> Stream<T> activeFilter(Stream<T> inStream) {
		checkNotNull(inStream);
		return inStream.filter(ACTIVE_OBJECT_PREDICATE);
	}

	/**
	 * 
	 * @param inList
	 * @return
	 */
	public static <T extends ActiveObject> List<T> activeFilter(List<T> inList) {
		return filterToList(inList, ACTIVE_OBJECT_PREDICATE);
	}

	/**
	 * 
	 * @param inList
	 * @param predicate
	 * @return
	 */
	public static <T> List<T> filterToList(List<T> inList, Predicate<? super T> predicate) {
		return inList.stream().filter(predicate).collect(Collectors.toList());
	}

}
