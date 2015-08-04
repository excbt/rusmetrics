package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "cityFiasUUID", "cityName", "cityGeoPosX", "cityGeoPosY" })
public abstract class CityContObjects<T extends ContObjectHolder> implements
		Serializable {

	interface CityContObjectsFactory<T extends CityContObjects<? extends ContObjectHolder>> {
		public T newInstance(UUID uuid);
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7274020492051549617L;

	protected final UUID cityFiasUUID;

	protected final List<T> cityObjects = new ArrayList<>();

	/**
	 * 
	 * @param cityFiasUUID
	 */
	protected CityContObjects(UUID cityFiasUUID) {
		this.cityFiasUUID = cityFiasUUID;
	}

	/**
	 * 
	 * @return
	 */

	protected List<T> getCityObjects() {
		return this.cityObjects;
	}

	/**
	 * 
	 * @return
	 */
	public UUID getCityFiasUUID() {
		return cityFiasUUID;
	}

	/**
	 * 
	 * @return
	 */
	public String getCityName() {
		Optional<T> item = cityObjects
				.stream()
				.filter((i) -> i.getContObject().getContObjectFias() != null
						&& i.getContObject().getContObjectFias()
								.getShortAddress2() != null).findFirst();
		return item.isPresent() ? item.get().getContObject()
				.getContObjectFias().getShortAddress2() : null;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCityGeoPosX() {
		Optional<T> item = cityObjects
				.stream()
				.filter((i) -> i.getContObject().getContObjectGeo() != null
						&& i.getContObject().getContObjectGeo()
								.getCityGeoPosX() != null).findFirst();
		return item.isPresent() ? item.get().getContObject().getContObjectGeo()
				.getCityGeoPosX() : null;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCityGeoPosY() {
		Optional<T> item = cityObjects
				.stream()
				.filter((i) -> i.getContObject().getContObjectGeo() != null
						&& i.getContObject().getContObjectGeo()
								.getCityGeoPosY() != null).findFirst();
		return item.isPresent() ? item.get().getContObject().getContObjectGeo()
				.getCityGeoPosY() : null;
	}

	/**
	 * 
	 * @return
	 */
	public static <T extends ContObjectHolder, U extends CityContObjects<T>> List<U> makeCityContObjects(
			Collection<? extends T> contObjects,
			CityContObjectsFactory<U> cityContObjectsFactory) {

		final Map<UUID, U> cityObjectsMap = new HashMap<>();

		contObjects
				.stream()
				.filter((i) -> i.getContObject().getContObjectFias() != null)
				.forEach(
						(i) -> {
							UUID cityUUID = i.getContObject()
									.getContObjectFias().getCityFiasUUID();
							U cityObject = cityObjectsMap.get(cityUUID);
							if (cityObject == null) {
								cityObject = cityContObjectsFactory
										.newInstance(cityUUID);
								if (cityObjectsMap.putIfAbsent(cityUUID,
										cityObject) != null) {
									throw new IllegalStateException(
											"cityObjectsMap error collapse");
								}
							}
							cityObject.getCityObjects().add(i);
						});

		return new ArrayList<>(cityObjectsMap.values());
	}

}
