package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * Элементы прайс-листа
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.11.2015
 *
 */
@Entity
@Table(name = "subscr_price_item", schema = DBMetadata.DB_SCHEME)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SubscrPriceItem extends AbstractAuditableModel implements DeletableObject {

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

	@Column(name = "deleted")
	private int deleted;

	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "is_single_price")
	private Boolean isSinglePrice;

}
