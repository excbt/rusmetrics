package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Тип тарифа
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.04.2015
 *
 */
@Entity
@Table(name = "tariff_type")
public class TariffType extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7252478781604008586L;

	@Column(name = "tariff_type_name")
	private String tariffTypeName;

	@Column(name = "cont_service_type")
	private String contServiceType;

	@Column(name = "tariff_type_unit")
	private String tariffTypeUnit;

	@Column(name = "tariff_type_capacity")
	private int tariffTypeCapacity;

	@Column(name = "tariff_type_order")
	private int tariffTypeOrder;

	public String getTariffTypeName() {
		return tariffTypeName;
	}

	public void setTariffTypeName(String tariffTypeName) {
		this.tariffTypeName = tariffTypeName;
	}

	public String getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(String contServiceType) {
		this.contServiceType = contServiceType;
	}

	public String getTariffTypeUnit() {
		return tariffTypeUnit;
	}

	public void setTariffTypeUnit(String tariffTypeUnit) {
		this.tariffTypeUnit = tariffTypeUnit;
	}

	public int getTariffTypeCapacity() {
		return tariffTypeCapacity;
	}

	public void setTariffTypeCapacity(int tariffTypeCapacity) {
		this.tariffTypeCapacity = tariffTypeCapacity;
	}

	public int getTariffTypeOrder() {
		return tariffTypeOrder;
	}

	public void setTariffTypeOrder(int tariffTypeOrder) {
		this.tariffTypeOrder = tariffTypeOrder;
	}

}
