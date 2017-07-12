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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.model.markers.ActiveObject;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

/**
 * Тип услуги абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
@Entity
@Table(name = "subscr_service_item")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class SubscrServiceItem extends AbstractAuditableModel implements KeynameObject, ActiveObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "item_caption")
	private String itemCaption;

	@Column(name = "item_description")
	private String itemDescription;

	@Column(name = "item_comment")
	@JsonIgnore
	private String itemComment;

	@Column(name = "item_category")
	@JsonIgnore
	private String itemCategory;

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

	@Column(name = "item_nr")
	private String itemNr;

	@Column(name = "item_order")
	private Integer itemOrder;

	@Column(name = "keyname")
	private String keyname;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_service_item_permission", joinColumns = @JoinColumn(name = "subscr_service_item_id") ,
			inverseJoinColumns = @JoinColumn(name = "subscr_service_permission") )
	private List<SubscrServicePermission> servicePermissions = new ArrayList<>();

}
