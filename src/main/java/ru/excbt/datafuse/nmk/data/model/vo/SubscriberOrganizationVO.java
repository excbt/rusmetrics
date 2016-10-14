/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.vo;

import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ModelWrapper;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 * 
 */
public class SubscriberOrganizationVO extends ModelWrapper<Subscriber> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5227815402971206551L;

	private Organization organization;

	/**
	 * @param model
	 */
	public SubscriberOrganizationVO(Subscriber model) {
		super(model);
	}

	/**
	 * 
	 * @param model
	 * @param organization
	 */
	public SubscriberOrganizationVO(Subscriber model, Organization organization) {
		super(model);
		this.organization = organization;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
