package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_object_tree")
@Getter
@Setter
public class SubscrObjectTree extends JsonAbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -7126312877491219773L;

	@Column(name = "parent_id", insertable = false, updatable = false)
	private Long parentId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private SubscrObjectTree parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
	private List<SubscrObjectTree> childObjectList = new ArrayList<SubscrObjectTree>();

	@Column(name = "rma_subscriber_id")
	private Long rmaSubscriberId;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "is_rma")
	private Boolean isRma;

	@Column(name = "object_tree_type")
	private String objectTreeType;

	@Column(name = "object_name")
	private String objectName;

	@Column(name = "object_description")
	private String objectDescription;

	@Column(name = "object_comemnt")
	private String objectComemnt;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "template_id")
	private Long templateId;

	@Column(name = "template_item_id")
	private Long templateItemId;

	@Column(name = "is_link_deny")
	private Boolean isLinkDeny;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

}
