package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.excbt.datafuse.nmk.data.model.ContObject;

public class ContObjectServiceTypeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5952850221897310120L;

	private final ContObjectShort contObject;

	private final List<ServiceTypeART> serviceTypeARTs = new ArrayList<>();

	/**
	 * 
	 * @param contObject
	 */
	public ContObjectServiceTypeInfo(ContObject contObject) {
		this.contObject = ContObjectShort.newInstance(contObject);
	}

	/**
	 * 
	 * @return
	 */
	public ContObjectShort getContObject() {
		return contObject;
	}

	/**
	 * 
	 * @return
	 */
	public List<ServiceTypeART> getServiceTypeARTs() {
		return Collections.unmodifiableList(serviceTypeARTs);
	}

	/**
	 * 
	 * @param serviceTypeKeyname
	 * @return
	 */
	public ServiceTypeART addServiceTypeART(String serviceTypeKeyname) {
		ServiceTypeART item = new ServiceTypeART(serviceTypeKeyname);
		serviceTypeARTs.add(item);
		return item;
	}
}
