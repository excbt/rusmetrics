/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.vo;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.support.ModelWrapper;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 * 
 */
public class ContObjectVOFias extends ModelWrapper<ContObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6651921688983257995L;

	private final ContObjectFias contObjectFias;

	private final ContObjectGeoPos contObjectGeo;

	/**
	 * @param model
	 */
	public ContObjectVOFias(ContObject model, ContObjectFias contObjectFias) {
		super(model);
		this.contObjectFias = contObjectFias;
		this.contObjectGeo = null;
	}

	/**
	 * 
	 * @param model
	 * @param contObjectFias
	 * @param contObjectGeo
	 */
	public ContObjectVOFias(ContObject model, ContObjectFias contObjectFias, ContObjectGeoPos contObjectGeo) {
		super(model);
		this.contObjectFias = contObjectFias;
		this.contObjectGeo = contObjectGeo;
	}

	/**
	 * 
	 * @return
	 */
	public ContObjectFias getContObjectFias() {
		return contObjectFias;
	}

	/**
	 * 
	 * @return
	 */
	public ContObjectGeoPos getContObjectGeo() {
		return contObjectGeo;
	}

}
