package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

/**
 * Роль абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "subscr_role")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
public class SubscrRole extends JsonAbstractAuditableModel implements DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "subscr_role_name")
	private String roleName;

	@Column(name = "subscr_role_info")
	private String info;

	@Column(name = "subscr_role_comment")
	private String comment;

	@JsonIgnore
	@Version
	private int version;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

}
