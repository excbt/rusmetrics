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

	public Long getSrcPriceListId() {
		return srcPriceListId;
	}

	public void setSrcPriceListId(Long srcPriceListId) {
		this.srcPriceListId = srcPriceListId;
	}

	public String getPriceListName() {
		return priceListName;
	}

	public void setPriceListName(String priceListName) {
		this.priceListName = priceListName;
	}

	public Subscriber getRmaSubscriber() {
		return rmaSubscriber;
	}

	public void setRmaSubscriber(Subscriber rmaSubscriber) {
		this.rmaSubscriber = rmaSubscriber;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Integer getPriceListLevel() {
		return priceListLevel;
	}

	public void setPriceListLevel(Integer priceListLevel) {
		this.priceListLevel = priceListLevel;
	}

	public Integer getPriceListType() {
		return priceListType;
	}

	public void setPriceListType(Integer priceListType) {
		this.priceListType = priceListType;
	}

	public String getPriceOption() {
		return priceOption;
	}

	public void setPriceOption(String priceOption) {
		this.priceOption = priceOption;
	}

	public Date getPlanBeginDate() {
		return planBeginDate;
	}

	public void setPlanBeginDate(Date planBeginDate) {
		this.planBeginDate = planBeginDate;
	}

	public Date getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(Date planEndDate) {
		this.planEndDate = planEndDate;
	}

	public Date getFactBeginDate() {
		return factBeginDate;
	}

	public void setFactBeginDate(Date factBeginDate) {
		this.factBeginDate = factBeginDate;
	}

	public Date getFactEndDate() {
		return factEndDate;
	}

	public void setFactEndDate(Date factEndDate) {
		this.factEndDate = factEndDate;
	}

	@Override
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(Boolean isMaster) {
		this.isMaster = isMaster;
	}

	public Boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}

	public Boolean getIsArchive() {
		return isArchive;
	}

	public void setIsArchive(Boolean isArchive) {
		this.isArchive = isArchive;
	}

	public Long getMasterPriceListId() {
		return masterPriceListId;
	}

	public void setMasterPriceListId(Long masterPriceListId) {
		this.masterPriceListId = masterPriceListId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Long getRmaSubscriberId() {
		return rmaSubscriberId;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public String getPriceListKeyname() {
		return priceListKeyname;
	}

	public void setPriceListKeyname(String priceListKeyname) {
		this.priceListKeyname = priceListKeyname;
	}

	public String getPriceListCurrency() {
		return priceListCurrency;
	}

	public void setPriceListCurrency(String priceListCurrency) {
		this.priceListCurrency = priceListCurrency;
	}

}
