package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "subscr_service_permission")
@JsonInclude(Include.NON_NULL)
public class SubscrServicePermission extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1458165553195737365L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "permission_name")
	private String permissionName;

	@Column(name = "permission_description")
	@JsonIgnore
	private String permissionDescription;

	@Column(name = "permission_comment")
	@JsonIgnore
	private String permissionComment;

	@Column(name = "help_context")
	private String helpContext;

	@Column(name = "permission_object_class")
	private String permissionObjectClass;

	@Column(name = "permission_object_keyname")
	private String permissionObjectKeyname;

	@Column(name = "permission_object_id")
	private Long permissionObjectId;

	@Column(name = "permission_help_context")
	private String permissoinHelpContext;

	@Column(name = "is_front")
	private Boolean isFront;

	@Column(name = "is_deny")
	private Boolean isDeny;

	@Column(name = "is_common")
	private Boolean isCommon;

	@Column(name = "priority")
	private Integer priority;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getPermissionDescription() {
		return permissionDescription;
	}

	public void setPermissionDescription(String permissionDescription) {
		this.permissionDescription = permissionDescription;
	}

	public String getPermissionComment() {
		return permissionComment;
	}

	public void setPermissionComment(String permissionComment) {
		this.permissionComment = permissionComment;
	}

	public String getHelpContext() {
		return helpContext;
	}

	public void setHelpContext(String helpContext) {
		this.helpContext = helpContext;
	}

	public String getPermissionObjectClass() {
		return permissionObjectClass;
	}

	public void setPermissionObjectClass(String permissionObjectClass) {
		this.permissionObjectClass = permissionObjectClass;
	}

	public String getPermissionObjectKeyname() {
		return permissionObjectKeyname;
	}

	public void setPermissionObjectKeyname(String permissionObjectKeyname) {
		this.permissionObjectKeyname = permissionObjectKeyname;
	}

	public Long getPermissionObjectId() {
		return permissionObjectId;
	}

	public void setPermissionObjectId(Long permissionObjectId) {
		this.permissionObjectId = permissionObjectId;
	}

	public String getPermissoinHelpContext() {
		return permissoinHelpContext;
	}

	public void setPermissoinHelpContext(String permissoinHelpContext) {
		this.permissoinHelpContext = permissoinHelpContext;
	}

	public Boolean getIsFront() {
		return isFront;
	}

	public void setIsFront(Boolean isFront) {
		this.isFront = isFront;
	}

	public Boolean getIsDeny() {
		return isDeny;
	}

	public void setIsDeny(Boolean isDeny) {
		this.isDeny = isDeny;
	}

	public Boolean getIsCommon() {
		return isCommon;
	}

	public void setIsCommon(Boolean isCommon) {
		this.isCommon = isCommon;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
