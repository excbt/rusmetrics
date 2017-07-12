package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Кросс пользователи - группы рассылок для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
@Entity
@Table(name = "subscr_action_user_group")
@Getter
@Setter
public class SubscrActionUserGroup extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3198493937130028852L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_action_group_id", updatable = false, insertable = false)
	@JsonIgnore
	private SubscrActionGroup subscrActionGroup;

	@Column(name = "subscr_action_group_id")
	private Long subscrActionGroupId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_action_user_id", updatable = false, insertable = false)
	@JsonIgnore
	private SubscrActionUser subscrActionUser;

	@Column(name = "subscr_action_user_id")
	private Long subscrActionUserId;

}
