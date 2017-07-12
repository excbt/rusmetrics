package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

/**
 * Настройка адресов абонента для СМС сообщения
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@Entity
@Table(name = "subscr_cont_event_type_sms_addr")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class SubscrContEventTypeSmsAddr extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -8557265399290800488L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_cont_event_type_sms_id")
	private SubscrContEventTypeSms subscrContEventTypeSms;

	@Column(name = "subscr_cont_event_type_sms_id", insertable = false, updatable = false)
	private Long subscrContEventTypeSmsId;

	@Column(name = "address_name")
	private String addressName;

	@Column(name = "address_type")
	private String addressType = "sms";

	@Column(name = "address_sms")
	private String addressSms;

	@Column(name = "address_email")
	private String addressEmail;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

}
