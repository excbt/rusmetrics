package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Пакет услуг абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
@Entity
@Table(name = "subscr_service_pack")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrServicePack extends JsonAbstractAuditableModel implements KeynameObject, ActiveObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 4359901990805548362L;

	@Column(name = "pack_name")
	private String packName;

	@Column(name = "pack_caption")
	private String packCaption;

	@Column(name = "pack_description")
	private String packDescription;

	@Column(name = "pack_comment")
	private String packComment;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "active_start_date")
	@Temporal(TemporalType.DATE)
	private Date activeStartDate;

	@Column(name = "active_end_date")
	@Temporal(TemporalType.DATE)
	private Date activeEndDate;

	@Column(name = "help_context")
	private String helpContext;

	@Column(name = "pack_nr")
	private String packNr;

	@Column(name = "pack_order")
	private Integer packOrder;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_service_pack_item", joinColumns = @JoinColumn(name = "subscr_service_pack_id"),
			inverseJoinColumns = @JoinColumn(name = "subscr_service_item_id"))
	private List<SubscrServiceItem> serviceItems = new ArrayList<>();

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "is_persistent_service")
	private Boolean isPersistentService;

	@Column(name = "is_special")
	private Boolean isSpecial;

}
