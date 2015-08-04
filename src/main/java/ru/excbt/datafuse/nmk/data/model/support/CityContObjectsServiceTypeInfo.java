package ru.excbt.datafuse.nmk.data.model.support;

import java.util.List;
import java.util.UUID;

public class CityContObjectsServiceTypeInfo extends
		CityContObjects<ContObjectServiceTypeInfo> {

	public static CityContObjectsServiceTypeInfoFactory FACTORY_INSTANCE = new CityContObjectsServiceTypeInfoFactory();

	public static class CityContObjectsServiceTypeInfoFactory implements
			CityContObjectsFactory<CityContObjectsServiceTypeInfo> {

		@Override
		public CityContObjectsServiceTypeInfo newInstance(UUID uuid) {
			return new CityContObjectsServiceTypeInfo(uuid);
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4128928226356864846L;

	/**
	 * 
	 * @param cityFiasUUID
	 */
	protected CityContObjectsServiceTypeInfo(UUID cityFiasUUID) {
		super(cityFiasUUID);
	}

	/**
	 * 
	 * @return
	 */
	public List<ContObjectServiceTypeInfo> contObjectsServiceTypeInfo() {
		return this.cityObjects;
	}

	/**
	 * 
	 */
	@Override
	public List<ContObjectServiceTypeInfo> getCityObjects() {
		return super.getCityObjects();
	}

}
