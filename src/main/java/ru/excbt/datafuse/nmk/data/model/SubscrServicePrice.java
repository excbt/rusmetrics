package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Прайс лист пакетов и услуг
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
@Entity
@Table(name = "subscr_service_price")
@Getter
@Setter
public class SubscrServicePrice extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 578314712809691578L;

	@Column(name = "subscr_service_pack_id")
	private Long packId;

	@Column(name = "subscr_service_item_id")
	private Long itemId;

	@Column(name = "price_value", columnDefinition = "numeric")
	private Double priceValue;

	@Column(name = "price_option")
	private String priceOption;

	@Column(name = "price_begin_date")
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date priceBeginDate;

	@Column(name = "price_end_date")
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date priceEndDate;

}
