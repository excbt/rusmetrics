package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

@Entity
@Table(name = "subscr_service_item")
@JsonInclude(Include.NON_NULL)
public class SubscrServiceItem extends AbstractAuditableModel implements KeynameObject, ActiveObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "item_caption")
	private String itemCaption;

	@Column(name = "item_description")
	private String itemDescription;

	@Column(name = "item_comment")
	@JsonIgnore
	private String itemComment;

	@Column(name = "item_category")
	@JsonIgnore
	private String itemCategory;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "active_start_date")
	@Temporal(TemporalType.DATE)
	private Date activeStartDate;

	@Column(name = "active_end_date")
	@Temporal(TemporalType.DATE)
	private Date activeEndDate;

	@Column(name = "help_context")
	private String helpContext;

	@Column(name = "item_nr")
	private String itemNr;

	@Column(name = "item_order")
	private Integer itemOrder;

	@Column(name = "keyname")
	private String keyname;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_service_item_permission", joinColumns = @JoinColumn(name = "subscr_service_item_id") ,
			inverseJoinColumns = @JoinColumn(name = "subscr_service_permission") )
	private List<SubscrServicePermission> servicePermissions = new ArrayList<>();

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemCaption() {
		return itemCaption;
	}

	public void setItemCaption(String itemCaption) {
		this.itemCaption = itemCaption;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemComment() {
		return itemComment;
	}

	public void setItemComment(String itemComment) {
		this.itemComment = itemComment;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	@Override
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Date getActiveStartDate() {
		return activeStartDate;
	}

	public void setActiveStartDate(Date activeStartDate) {
		this.activeStartDate = activeStartDate;
	}

	public Date getActiveEndDate() {
		return activeEndDate;
	}

	public void setActiveEndDate(Date activeEndDate) {
		this.activeEndDate = activeEndDate;
	}

	public String getHelpContext() {
		return helpContext;
	}

	public void setHelpContext(String helpContext) {
		this.helpContext = helpContext;
	}

	public String getItemNr() {
		return itemNr;
	}

	public void setItemNr(String itemNr) {
		this.itemNr = itemNr;
	}

	public Integer getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(Integer itemOrder) {
		this.itemOrder = itemOrder;
	}

	@Override
	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public List<SubscrServicePermission> getServicePermissions() {
		return servicePermissions;
	}

	public void setServicePermissions(List<SubscrServicePermission> servicePermissions) {
		this.servicePermissions = servicePermissions;
	}

}
