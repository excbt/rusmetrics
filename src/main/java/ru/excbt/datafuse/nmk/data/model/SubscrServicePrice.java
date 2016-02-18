package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class SubscrServicePrice extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 578314712809691578L;

	@Column(name = "subscr_service_pack_id")
	private Long packId;

	@Column(name = "subscr_service_item_id")
	private Long itemId;

	@Column(name = "price_value")
	private BigDecimal priceValue;

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

	public Long getPackId() {
		return packId;
	}

	public void setPackId(Long packId) {
		this.packId = packId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

	public String getPriceOption() {
		return priceOption;
	}

	public void setPriceOption(String priceOption) {
		this.priceOption = priceOption;
	}

	public Date getPriceBeginDate() {
		return priceBeginDate;
	}

	public void setPriceBeginDate(Date priceBeginDate) {
		this.priceBeginDate = priceBeginDate;
	}

	public Date getPriceEndDate() {
		return priceEndDate;
	}

	public void setPriceEndDate(Date priceEndDate) {
		this.priceEndDate = priceEndDate;
	}

}
