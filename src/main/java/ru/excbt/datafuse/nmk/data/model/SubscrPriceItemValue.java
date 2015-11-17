package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "subscr_price_item_value")
public class SubscrPriceItemValue extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2329929788034927010L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_price_item_id")
	private SubscrPriceItem subcrPriceItem;

	@Column(name = "subscr_price_item_id", insertable = false, updatable = false)
	private Long subcrPriceItemId;

	@Column(name = "price_value")
	private BigDecimal priceValue;

	@Column(name = "value_begin_date")
	@Temporal(TemporalType.DATE)
	private Date valueBeginDate;

	@Column(name = "value_end_date")
	@Temporal(TemporalType.DATE)
	private Date valueEndDate;

	@Version
	@Column(name = "version")
	private int version;

	public SubscrPriceItem getSubcrPriceItem() {
		return subcrPriceItem;
	}

	public void setSubcrPriceItem(SubscrPriceItem subcrPriceItem) {
		this.subcrPriceItem = subcrPriceItem;
	}

	public BigDecimal getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

	public Date getValueBeginDate() {
		return valueBeginDate;
	}

	public void setValueBeginDate(Date valueBeginDate) {
		this.valueBeginDate = valueBeginDate;
	}

	public Date getValueEndDate() {
		return valueEndDate;
	}

	public void setValueEndDate(Date valueEndDate) {
		this.valueEndDate = valueEndDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getSubcrPriceItemId() {
		return subcrPriceItemId;
	}

	public void setSubcrPriceItemId(Long subcrPriceItemId) {
		this.subcrPriceItemId = subcrPriceItemId;
	}

}
