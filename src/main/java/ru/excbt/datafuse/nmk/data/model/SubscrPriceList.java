package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = ModelTables.SUBSCR_PRICE_LIST)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscrPriceList extends AbstractAuditableModel implements DisabledObject, ActiveObject, DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6899333535833888171L;

	@Column(name = "src_price_list_id")
	private Long srcPriceListId;

	@Column(name = "price_list_name")
	private String priceListName;

	@Column(name = "subscriber_id_1")
	private Long subscriberId1;

	@Column(name = "subscriber_id_2")
	private Long subscriberId2;

	@Column(name = "subscriber_id_3")
	private Long subscriberId3;

	@Column(name = "price_list_level")
	private Integer priceListLevel;

	@Column(name = "price_list_type")
	private Integer priceListType;

	@Column(name = "price_list_option")
	private String priceListOption;

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
	private Boolean isActive;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "is_master")
	private Boolean isMaster;

	@Column(name = "is_draft")
	private Boolean isDraft = true;

	@Column(name = "is_archive")
	private Boolean isArchive = false;

	@Version
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

	public Long getSubscriberId1() {
		return subscriberId1;
	}

	public void setSubscriberId1(Long subscriberId1) {
		this.subscriberId1 = subscriberId1;
	}

	public Long getSubscriberId2() {
		return subscriberId2;
	}

	public void setSubscriberId2(Long subscriberId2) {
		this.subscriberId2 = subscriberId2;
	}

	public Long getSubscriberId3() {
		return subscriberId3;
	}

	public void setSubscriberId3(Long subscriberId3) {
		this.subscriberId3 = subscriberId3;
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

	public String getPriceListOption() {
		return priceListOption;
	}

	public void setPriceListOption(String priceListOption) {
		this.priceListOption = priceListOption;
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

}
