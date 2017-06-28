package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Связь Абонент-Универсальный справочник
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 *
 */
@Entity
@Table(name = "subscr_directory")
@Getter
@Setter
public class SubscrDirectory extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4169289603719698288L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id", updatable = false)
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_id")
	private UDirectory udirectory;

	@Column(name = "directory_id", insertable = false, updatable = false)
	private Long udirectoryId;

	@Version
	@Column(name = "version")
	private int version;

}
