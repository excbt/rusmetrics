package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "subscr_service_permission")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
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

	@JsonIgnore
	@Column(name = "permission_object_class")
	private String permissionObjectClass;

	@JsonIgnore
	@Column(name = "permission_object_keyname")
	private String permissionObjectKeyname;

	@JsonIgnore
	@Column(name = "permission_object_id")
	private Long permissionObjectId;

	@Column(name = "permission_help_context")
	private String permissoinHelpContext;

	@JsonIgnore
	@Column(name = "is_front")
	private Boolean isFront;

	@Column(name = "is_deny")
	private Boolean isDeny;

	@Column(name = "is_common")
	private Boolean isCommon;

	@Column(name = "priority")
	private Integer priority;

	@Column(name = "permission_tag_id")
	private String permissionTagId;

	@Column(name = "is_rma_filter")
	private Boolean isRmaFilter;

}
