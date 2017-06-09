package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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

}
