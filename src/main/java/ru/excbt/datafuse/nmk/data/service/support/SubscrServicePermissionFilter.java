package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
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
	public <T> List<T> filterObjects(List<T> objectList) {
		if (objectList.size() == 0) {
			return objectList;
		}
		Object obj = objectList.get(0);
		boolean keynameSupports = obj instanceof KeynameObject;

		List<T> resultObjectList = null;

		if (keynameSupports) {
			String className = obj.getClass().getSimpleName();
			List<String> keynames = getObjectKeynamesByClass(className);
			resultObjectList = keynameFilter(keynames, objectList);
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
	private List<String> objectKeynames(List<SubscrServicePermission> permissions) {
		List<String> result = new ArrayList<>();
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
	public List<String> getObjectKeynamesByClass(String className) {
		List<SubscrServicePermission> objectPermissions = permissionList.stream()
				.filter((i) -> className.equals(i.getPermissionObjectClass())).collect(Collectors.toList());
		return objectKeynames(objectPermissions);
	}

}
