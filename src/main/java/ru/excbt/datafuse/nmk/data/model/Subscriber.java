package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
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
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@DynamicUpdate
public class Subscriber extends JsonAbstractAuditableModel implements DeletableObject, PersistableBuilder<Subscriber, Long> {

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
    @BatchSize(size = 10)
	private Organization organization;

	@Column(name = "timezone_def")
	private String timezoneDefKeyname;

	@JsonIgnore
	@Column(name = "subscriber_uuid", updatable = false, columnDefinition = "uuid")
	private UUID subscriberUUID;

	@Version
	private int version;

	@Column(name = "is_rma", insertable = false, updatable = false)
	private Boolean isRma = false;

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

	@Column(name = "map_center_lat", columnDefinition = "numeric")
	private Double mapCenterLat;

	@Column(name = "map_center_lng", columnDefinition = "numeric")
	private Double mapCenterLng;

	@Column(name = "map_zoom", columnDefinition = "numeric")
	private Double mapZoom;

	@Column(name = "map_zoom_detail", columnDefinition = "numeric")
	private Double mapZoomDetail;

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

	@JsonIgnore
	@Column(name = "subscr_cabinet_seq")
	private String subscrCabinetSeq;

	@Column(name = "contact_email")
	private String contactEmail;

	@JsonIgnore
	@Column(name = "parent_subscriber_id", updatable = false)
	private Long parentSubscriberId;

}
