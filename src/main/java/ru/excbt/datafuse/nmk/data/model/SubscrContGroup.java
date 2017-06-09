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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ContGroupType;

/**
 * Группы объектов учета
 *
 * @author S.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_cont_group")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrContGroup extends AbstractAuditableModel {

	/**
		 *
		 */
	private static final long serialVersionUID = 9133971621136169339L;

	/**
		 *
		 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_group_type", insertable = false, updatable = false)
	private ContGroupType contGroupType;

	@Column(name = "cont_group_type")
	private String contGroupTypeKey;

	@Column(name = "cont_group_name")
	private String contGroupName;

	@Column(name = "cont_group_comment")
	private String contGroupComment;

	@Column(name = "cont_group_description")
	private String contGroupDescription;

	@Version
	private int version;

}
