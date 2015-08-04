package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

public class ContObjectServiceTypeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5952850221897310120L;

	private final ContObjectShort contObject;

	private final List<ContServiceTypeInfoART> serviceTypeARTs = new ArrayList<>();

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
	public List<ContServiceTypeInfoART> getServiceTypeARTs() {
		return Collections.unmodifiableList(serviceTypeARTs);
	}

	/**
	 * 
	 * @param serviceTypeKeyname
	 * @return
	 */
	public ContServiceTypeInfoART addServiceTypeART(String serviceTypeKeyname) {
		ContServiceTypeInfoART item = new ContServiceTypeInfoART(
				ContServiceTypeKey.searchKeyname(serviceTypeKeyname));
		serviceTypeARTs.add(item);
		return item;
	}

	/**
	 * 
	 * @param item
	 */
	public void addServiceTypeART(ContServiceTypeInfoART item) {
		checkNotNull(item);
		serviceTypeARTs.add(item);
	}
}
