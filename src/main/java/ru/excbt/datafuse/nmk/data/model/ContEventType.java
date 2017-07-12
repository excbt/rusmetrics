package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

/**
 * Тип событий контейнера
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.02.2015
 *
 */
@Entity
@Table(name = "cont_event_type")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class ContEventType extends AbstractPersistableEntity<Long> implements DevModeObject, DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "cont_event_type_name")
	private String name;

	@Column(name = "cont_event_type_comment")
	private String comment;

	@Column(name = "cont_event_type_description")
	private String description;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "parent_id")
	private Long parentId;

	@Column(name = "cont_event_level")
	private Integer contEventLevel;

	@Column(name = "cont_event_category", insertable = false, updatable = false)
	private String contEventCategory;

	@Column(name = "cont_event_category")
	private String contEventCategoryKeyname;

	@Column(name = "reverse_id")
	private Long reverseId;

	@Column(name = "is_base_event")
	private Boolean isBaseEvent;

	@Column(name = "is_critical_event")
	private Boolean isCriticalEvent;

	@Column(name = "is_scalar_event")
	private Boolean isScalarEvent;

	@Column(name = "is_dev_mode")
	private Boolean isDevMode;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "is_sms_notification")
	private Boolean isSmsNotification;

	@Column(name = "sms_message_template")
	private String smsMessageTemplate;

}
