package ru.excbt.datafuse.nmk.data.model;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

/**
 * Прайс лист абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.11.2015
 *
 */
@Entity
@Table(name = "subscr_price_list", schema = DBMetadata.DB_SCHEME)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SubscrPriceList extends AbstractAuditableModel implements DisabledObject, ActiveObject, DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -6899333535833888171L;

	@Column(name = "src_price_list_id", updatable = false)
	private Long srcPriceListId;

	@Column(name = "price_list_name")
	private String priceListName;

	@Column(name = "rma_subscriber_id", insertable = false, updatable = false)
	private Long rmaSubscriberId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rma_subscriber_id")
	private Subscriber rmaSubscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "price_list_level", updatable = false)
	private Integer priceListLevel;

	@Column(name = "price_list_type", updatable = false)
	private Integer priceListType;

	@Column(name = "price_list_keyname", updatable = false)
	private String priceListKeyname;

	@Column(name = "price_list_currency", updatable = false)
	private String priceListCurrency;

	@Column(name = "price_option", updatable = false)
	private String priceOption;

	@Column(name = "plan_begin_date")
	@Temporal(TemporalType.DATE)
	private Date planBeginDate;

	@Column(name = "plan_end_date")
	@Temporal(TemporalType.DATE)
	private Date planEndDate;

	@Column(name = "fact_begin_date")
	@Temporal(TemporalType.DATE)
	private Date factBeginDate;

	@Column(name = "fact_end_date")
	@Temporal(TemporalType.DATE)
	private Date factEndDate;

	@Column(name = "is_active")
	private Boolean isActive = false;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "is_master", updatable = false)
	private Boolean isMaster;

	@Column(name = "is_draft")
	private Boolean isDraft = true;

	@Column(name = "is_archive")
	private Boolean isArchive = false;

	@Column(name = "master_price_list_id", updatable = false)
	private Long masterPriceListId;

	@Version
	@Column(name = "version")
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
