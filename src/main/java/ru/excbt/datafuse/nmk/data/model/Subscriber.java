package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * Абонент
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.03.2015
 *
 */
@Entity
@Table(name = "subscriber")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Subscriber extends AbstractAuditableModel implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "subscriber_name", insertable = true, updatable = false)
	private String subscriberName;

	@Column(name = "subscriber_info")
	private String info;

	@Column(name = "subscriber_comment")
	private String comment;

	@Column(name = "organization_id")
	private Long organizationId;

	@Column(name = "timezone_def")
	private String timezoneDefKeyname;

	@JsonIgnore
	@Column(name = "subscriber_uuid", insertable = false, updatable = false)
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID subscriberUUID;

	@Version
	private int version;

	@Column(name = "is_rma", insertable = false, updatable = false)
	private Boolean isRma;

	@Column(name = "rma_subscriber_id", updatable = false)
	private Long rmaSubscriberId;

	@Column(name = "ghost_subscriber_id", insertable = false, updatable = false)
	private Long ghostSubscriberId;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@JsonIgnore
	@Column(name = "rma_ldap_ou", insertable = false, updatable = false)
	private String rmaLdapOu;

	@Column(name = "map_center_lat")
	private BigDecimal mapCenterLat;

	@Column(name = "map_center_lng")
	private BigDecimal mapCenterLng;

	@Column(name = "map_zoom")
	private BigDecimal mapZoom;

	@Column(name = "map_zoom_detail")
	private BigDecimal mapZoomDetail;

	@JsonIgnore
	@Column(name = "subscr_type", updatable = false)
	private String subscrType;

	@Column(name = "can_create_child")
	private Boolean canCreateChild;

	@Column(name = "is_child", updatable = false)
	private Boolean isChild;

	@JsonIgnore
	@Column(name = "child_ldap_ou", updatable = false)
	private String childLdapOu;

	@Column(name = "subscr_cabinet_nr")
	private String subscrCabinetNr;

	@Column(name = "contact_email")
	private String contactEmail;

	@JsonIgnore
	@Column(name = "parent_subscriber_id", updatable = false)
	private Long parentSubscriberId;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public UUID getSubscriberUUID() {
		return subscriberUUID;
	}

	public void setSubscriberUUID(UUID subscriberUUID) {
		this.subscriberUUID = subscriberUUID;
	}

	public Boolean getIsRma() {
		return isRma;
	}

	public void setIsRma(Boolean isRma) {
		this.isRma = isRma;
	}

	public Long getRmaSubscriberId() {
		return rmaSubscriberId;
	}

	public void setRmaSubscriberId(Long rmaSubscriberId) {
		this.rmaSubscriberId = rmaSubscriberId;
	}

	public Long getGhostSubscriberId() {
		return ghostSubscriberId;
	}

	public void setGhostSubscriberId(Long ghostSubscriberId) {
		this.ghostSubscriberId = ghostSubscriberId;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getRmaLdapOu() {
		return rmaLdapOu;
	}

	public void setRmaLdapOu(String rmaLdapOu) {
		this.rmaLdapOu = rmaLdapOu;
	}

	public String getTimezoneDefKeyname() {
		return timezoneDefKeyname;
	}

	public void setTimezoneDefKeyname(String timezoneDefKeyname) {
		this.timezoneDefKeyname = timezoneDefKeyname;
	}

	public BigDecimal getMapCenterLat() {
		return mapCenterLat;
	}

	public void setMapCenterLat(BigDecimal mapCenterLat) {
		this.mapCenterLat = mapCenterLat;
	}

	public BigDecimal getMapCenterLng() {
		return mapCenterLng;
	}

	public void setMapCenterLng(BigDecimal mapCenterLng) {
		this.mapCenterLng = mapCenterLng;
	}

	public BigDecimal getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(BigDecimal mapZoom) {
		this.mapZoom = mapZoom;
	}

	public BigDecimal getMapZoomDetail() {
		return mapZoomDetail;
	}

	public void setMapZoomDetail(BigDecimal mapZoomDetail) {
		this.mapZoomDetail = mapZoomDetail;
	}

	public String getSubscrType() {
		return subscrType;
	}

	public void setSubscrType(String subscrType) {
		this.subscrType = subscrType;
	}

	public Boolean getCanCreateChild() {
		return canCreateChild;
	}

	public void setCanCreateChild(Boolean canCreateChild) {
		this.canCreateChild = canCreateChild;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getChildLdapOu() {
		return childLdapOu;
	}

	public void setChildLdapOu(String childLdapOu) {
		this.childLdapOu = childLdapOu;
	}

	public String getSubscrCabinetNr() {
		return subscrCabinetNr;
	}

	public void setSubscrCabinetNr(String subscrCabinetNr) {
		this.subscrCabinetNr = subscrCabinetNr;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Long getParentSubscriberId() {
		return parentSubscriberId;
	}

	public void setParentSubscriberId(Long parentSubscriberId) {
		this.parentSubscriberId = parentSubscriberId;
	}

}
