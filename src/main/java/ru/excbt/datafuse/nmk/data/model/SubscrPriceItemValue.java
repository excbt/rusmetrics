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

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * История значений цен
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.11.2015
 *
 */
@Entity
@Table(name = "subscr_price_item_value")
@Getter
@Setter
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

	@Column(name = "price_value", columnDefinition = "numeric")
	private Double priceValue;

	@Column(name = "value_begin_date")
	@Temporal(TemporalType.DATE)
	private Date valueBeginDate;

	@Column(name = "value_end_date")
	@Temporal(TemporalType.DATE)
	private Date valueEndDate;

	@Version
	@Column(name = "version")
	private int version;

}
