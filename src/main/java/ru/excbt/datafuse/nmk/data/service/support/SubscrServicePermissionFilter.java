package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Класс для работы с разрешениями абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.09.2015
 *
 */
public class SubscrServicePermissionFilter {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServicePermissionFilter.class);

	private final List<SubscrServicePermission> permissionList = new ArrayList<>();
	private final boolean isRma;

	private final SubscriberParam subscriberParam;

	/**
	 * 
	 * @param permissionList
	 * @param isRma
	 */
	public SubscrServicePermissionFilter(List<SubscrServicePermission> permissionList,
			SubscriberParam subscriberParam) {
		checkNotNull(permissionList);
		this.permissionList.addAll(permissionList);
		this.subscriberParam = subscriberParam;
		this.isRma = subscriberParam.isRma();
	}

	/**
	 * 
	 * @param permissionList
	 */
	//	public SubscrServicePermissionFilter(List<SubscrServicePermission> permissionList) {
	//		this(permissionList, false);
	//	}

	/**
	 * 
	 * @param objectList
	 * @return
	 */
	public <T> List<T> filterObjects(List<T> objectList) {
		if (objectList.size() == 0) {
			return new ArrayList<>(objectList);
		}
		Object obj = objectList.get(0);
		boolean keynameSupports = obj instanceof KeynameObject;

		List<T> resultObjectList = null;

		if (keynameSupports) {
			String className = obj.getClass().getSimpleName();
			List<String> keynames = filterKeynamesByClass(className);
			resultObjectList = keynameFilter(keynames, objectList);
		} else {
			resultObjectList = new ArrayList<>(objectList);
		}

		return resultObjectList;
	}

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private Collection<String> objectKeynames(List<SubscrServicePermission> permissions) {
		Set<String> result = new HashSet<>();
		permissions.stream().sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority())).forEach((i) -> {
			if (i.getPermissionObjectKeyname() != null) {
				if (Boolean.TRUE.equals(i.getIsDeny())) {
					result.remove(i.getPermissionObjectKeyname());
				} else {
					result.add(i.getPermissionObjectKeyname());
				}
			}
		});
		return result;
	}

	/**
	 * 
	 * @param objectList
	 * @param keynames
	 * @return
	 */
	private <T> List<T> keynameFilter(Collection<String> keynames, List<T> objectList) {
		return objectList.stream().filter((i) -> {
			if (i instanceof KeynameObject) {
				KeynameObject keynameObj = (KeynameObject) i;
				return keynames.contains(keynameObj.getKeyname());
			}
			return false;
		}).collect(Collectors.toList());

	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	public List<String> filterKeynamesByClass(String className) {
		checkNotNull(className, "class name is null");
		checkState(permissionList != null, "permissionList is not initialized");
		List<SubscrServicePermission> objectPermissions = permissionList.stream()
				.filter((i) -> className.equals(i.getPermissionObjectClass()))
				.filter((i) -> i.getIsRmaFilter() == null || i.getIsRmaFilter() == isRma).collect(Collectors.toList());
		return new ArrayList<>(objectKeynames(objectPermissions));
	}

}
