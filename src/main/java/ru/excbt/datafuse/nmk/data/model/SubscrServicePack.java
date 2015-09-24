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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "subscr_service_pack")
@JsonInclude(Include.NON_NULL)
public class SubscrServicePack extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4359901990805548362L;

	@Column(name = "pack_name")
	private String packName;

	@Column(name = "pack_caption")
	private String packCaption;

	@Column(name = "pack_description")
	private String packDescription;

	@Column(name = "pack_comment")
	private String packComment;

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

	@Column(name = "pack_nr")
	private String packNr;

	@Column(name = "pack_order")
	private Integer packOrder;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_service_pack_item", joinColumns = @JoinColumn(name = "subscr_service_pack_id") ,
			inverseJoinColumns = @JoinColumn(name = "subscr_service_item_id") )
	private List<SubscrServiceItem> serviceItems = new ArrayList<>();

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getPackCaption() {
		return packCaption;
	}

	public void setPackCaption(String packCaption) {
		this.packCaption = packCaption;
	}

	public String getPackDescription() {
		return packDescription;
	}

	public void setPackDescription(String packDescription) {
		this.packDescription = packDescription;
	}

	public String getPackComment() {
		return packComment;
	}

	public void setPackComment(String packComment) {
		this.packComment = packComment;
	}

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

	public List<SubscrServiceItem> getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(List<SubscrServiceItem> serviceItems) {
		this.serviceItems = serviceItems;
	}

	public Integer getPackOrder() {
		return packOrder;
	}

	public void setPackOrder(Integer packOrder) {
		this.packOrder = packOrder;
	}

	public String getPackNr() {
		return packNr;
	}

	public void setPackNr(String packNr) {
		this.packNr = packNr;
	}

}
