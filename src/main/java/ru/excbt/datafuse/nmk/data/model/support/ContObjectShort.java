package ru.excbt.datafuse.nmk.data.model.support;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import ru.excbt.datafuse.nmk.data.model.ContObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "contManagements", "owner", "ownerContacts",
		"version", "comment", "description" })
public class ContObjectShort extends ContObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4870131725310849947L;

	public static ContObjectShort newInstance(ContObject contObject) {
		ContObjectShort result = new ContObjectShort();
		try {
			BeanUtils.copyProperties(result, contObject);
		} catch (IllegalAccessException | InvocationTargetException e) {
			result = null;
		}
		return result;
	}
}
