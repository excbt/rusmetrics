package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.model.vo.ContObjectVOFias;

public class ContObjectServiceTypeInfo
		implements ContObjectHolder, ContObjectFiasHolder, ContObjectGeoPosHolder, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5952850221897310120L;

	private final ContObjectVOFias contObjectVOFias;

	private final List<ContServiceTypeInfoART> serviceTypeARTs = new ArrayList<>();

	/**
	 * 
	 * @param contObject
	 */
	public ContObjectServiceTypeInfo(ContObject contObject, ContObjectFias contObjectFias) {
		this.contObjectVOFias = new ContObjectVOFias(ContObjectShort.newInstance(contObject), contObjectFias);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public ContObject getContObject() {
		return contObjectVOFias.getModel();
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
		ContServiceTypeInfoART item = new ContServiceTypeInfoART(ContServiceTypeKey.searchKeyname(serviceTypeKeyname));
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

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.ContObjectFiasHolder#getContObjectFias()
	 */
	@Override
	public ContObjectFias getContObjectFias() {
		return contObjectVOFias.getContObjectFias();
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.data.model.support.ContObjectGeoPosHolder#getContObjectGeo()
	 */
	@Override
	public ContObjectGeoPos getContObjectGeo() {
		return contObjectVOFias.getContObjectGeo();
	}
}
