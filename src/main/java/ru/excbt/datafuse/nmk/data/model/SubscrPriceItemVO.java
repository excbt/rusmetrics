package ru.excbt.datafuse.nmk.data.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value Object для SubscrPriceItem
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.11.2015
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscrPriceItemVO {

	private final Long id;
	private final Long packId;
	private final Long itemId;
	private Double priceValue;
	private String currency;

	/**
	 *
	 * @param subscrPriceItem
	 */
	public SubscrPriceItemVO(SubscrPriceItem subscrPriceItem) {
		checkNotNull(subscrPriceItem);
		this.id = subscrPriceItem.getId();
		this.packId = subscrPriceItem.getSubscrServicePackId();
		this.itemId = subscrPriceItem.getSubscrServiceItemId();
		this.priceValue = subscrPriceItem.getPriceValue();
	}

	/**
	 *
	 * @param srcVO
	 */
	public SubscrPriceItemVO(SubscrPriceItemVO srcVO) {
		checkNotNull(srcVO);
		this.id = srcVO.id;
		this.packId = srcVO.packId;
		this.itemId = srcVO.itemId;
		this.priceValue = srcVO.priceValue;
	}

	/**
	 *
	 * @param srcVO
	 * @param newValue
	 */
	public SubscrPriceItemVO(SubscrPriceItemVO srcVO, Double newValue) {
		checkNotNull(srcVO);
		this.id = srcVO.id;
		this.packId = srcVO.packId;
		this.itemId = srcVO.itemId;
		this.priceValue = newValue;
	}

	/**
	 *
	 * @param id
	 * @param packId
	 * @param itemId
	 * @param value
	 */
	@JsonCreator
	public SubscrPriceItemVO(@JsonProperty("id") Long id, @JsonProperty("packId") Long packId,
			@JsonProperty("itemId") Long itemId, @JsonProperty("priceValue") Double priceValue) {
		this.id = id;
		this.packId = packId;
		this.itemId = itemId;
		this.priceValue = priceValue;
	}

	public Long getId() {
		return id;
	}

	public Long getPackId() {
		return packId;
	}

	public Long getItemId() {
		return itemId;
	}

	public Double getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(Double priceValue) {
		this.priceValue = priceValue;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
