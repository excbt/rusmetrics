package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Группы рассылок абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
@Entity
@Table(name = "subscr_action_group")
@Getter
@Setter
public class SubscrActionGroup extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -3946293470577245148L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "group_description")
	private String groupDescription;

	@Column(name = "group_comment")
	private String groupComment;

	@Version
	private int version;

}
