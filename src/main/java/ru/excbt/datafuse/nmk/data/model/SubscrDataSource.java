package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * Источник данных абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
@Entity
@Table(name = "subscr_data_source")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrDataSource extends JsonAbstractAuditableModel implements DeletableObject, PersistableBuilder<SubscrDataSource, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = -200221160904201276L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "data_source_type", insertable = false, updatable = false)
	private String dataSourceTypeKey;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "data_source_type")
	private DataSourceType dataSourceType;

	@Column(name = "data_source_name")
	private String dataSourceName;

	@Column(name = "data_source_description")
	private String dataSourceDescription;

	@Column(name = "data_source_comment")
	private String dataSourceComment;

	@Column(name = "data_source_ip")
	private String dataSourceIp;

	@Column(name = "data_source_port")
	private String dataSourcePort;

	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "deleted")
	@JsonIgnore
	private int deleted;

	@Column(name = "db_name")
	private String dbName;

	@Column(name = "db_user")
	private String dbUser;

	@Column(name = "db_password")
	private String dbPassword;

	@Transient
	private Boolean _isAnotherSubscriber;

	@Column(name = "raw_timeout")
	private Integer rawTimeout = 5000;

	@Column(name = "raw_sleep_time")
	private Integer rawSleepTime = 100;

	@Column(name = "raw_resend_attempts")
	private Integer rawResendAttempts = 1;

	@Column(name = "raw_reconnect_attempts")
	private Integer rawReconnectAttempts = 2;

	@Column(name = "raw_reconnect_timeout")
	private Integer rawReconnectTimeout = 90000;

	@Column(name = "raw_connection_type")
	private String rawConnectionType;

	@Column(name = "raw_modem_model_id")
	private Long rawModemModelId;

	@Column(name = "raw_modem_serial")
	private String rawModemSerial;

	@Column(name = "raw_modem_mac_addr")
	private String rawModemMacAddr;

	@Column(name = "raw_modem_imei")
	private String rawModemImei;

	@Column(name = "raw_modem_dial_enable")
	private Boolean rawModemDialEnable;

	@Column(name = "raw_modem_dial_tel")
	private String rawModemDialTel;

}
