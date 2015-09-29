package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public class SubscrServicePermissionFilter {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServicePermissionFilter.class);

	private final List<SubscrServicePermission> permissionList = new ArrayList<>();

	public SubscrServicePermissionFilter(List<SubscrServicePermission> permissionList) {
		checkNotNull(permissionList);
		this.permissionList.addAll(permissionList);
	}

	/**
	 * 
	 * @param objectList
	 * @return
	 */
	public <T> List<T> filterPermissions(List<T> objectList) {
		if (objectList.size() == 0) {
			return objectList;
		}
		Object obj = objectList.get(0);
		boolean keynameSupports = obj instanceof KeynameObject;

		String className = obj.getClass().getSimpleName();
		logger.debug("Class Name: {}. keynameSupports:{}", className, keynameSupports);

		List<SubscrServicePermission> actualPermissions = permissionList.stream()
				.filter((i) -> className.equals(i.getPermissionObjectClass())).collect(Collectors.toList());

		logger.debug("Count Permissions for {} is {} ", className, actualPermissions.size());

		List<T> resultObjectList = null;

		if (keynameSupports) {

			HashSet<String> keynames = permissionObjectKeynames(actualPermissions);
			// resultObjectList =
		} else {
			resultObjectList = objectList;
		}

		return resultObjectList;
	}

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private HashSet<String> permissionObjectKeynames(List<SubscrServicePermission> permissions) {
		HashSet<String> result = new HashSet<>();
		permissions.forEach((i) -> {
			if (i.getPermissionObjectKeyname() != null) {
				result.add(i.getPermissionObjectKeyname());
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
	private <T> List<T> keynameFilter(HashSet<String> keynames, List<T> objectList) {
		return objectList.stream().filter((i) -> {
			if (keynames instanceof KeynameObject) {
				KeynameObject keynameObj = (KeynameObject) i;
				return keynames.contains(keynameObj.getKeyname());
			}
			return false;
		}).collect(Collectors.toList());

	}

}
