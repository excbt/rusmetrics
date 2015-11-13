package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = ModelTables.SUBSCR_PRICE_ITEM)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscrPriceItem extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6673752581290477815L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_price_list_id")
	private SubscrPriceList subscrPriceList;

	@Column(name = "subscr_price_list_id", insertable = false, updatable = false)
	private Long subscrPriceListId;

	@Column(name = "src_price_item_id")
	private Long srcPriceItemId;

	@Column(name = "subscr_service_pack_id")
	private Long subscrServicePackId;

	@Column(name = "subscr_service_item_id")
	private Long subscrServiceItemId;

	@Column(name = "price_option")
	private String priceOption;

	@Column(name = "price_value")
	private BigDecimal priceValue;

	public SubscrPriceList getSubscrPriceList() {
		return subscrPriceList;
	}

	public void setSubscrPriceList(SubscrPriceList subscrPriceList) {
		this.subscrPriceList = subscrPriceList;
	}

	public Long getSubscrPriceListId() {
		return subscrPriceListId;
	}

	public void setSubscrPriceListId(Long subscrPriceListId) {
		this.subscrPriceListId = subscrPriceListId;
	}

	public Long getSrcPriceItemId() {
		return srcPriceItemId;
	}

	public void setSrcPriceItemId(Long srcPriceItemId) {
		this.srcPriceItemId = srcPriceItemId;
	}

	public Long getSubscrServicePackId() {
		return subscrServicePackId;
	}

	public void setSubscrServicePackId(Long subscrServicePackId) {
		this.subscrServicePackId = subscrServicePackId;
	}

	public Long getSubscrServiceItemId() {
		return subscrServiceItemId;
	}

	public void setSubscrServiceItemId(Long subscrServiceItemId) {
		this.subscrServiceItemId = subscrServiceItemId;
	}

	public String getPriceOption() {
		return priceOption;
	}

	public void setPriceOption(String priceOption) {
		this.priceOption = priceOption;
	}

	public BigDecimal getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

}