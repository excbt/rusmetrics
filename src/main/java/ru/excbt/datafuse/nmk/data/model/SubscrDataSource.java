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

import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
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
public class SubscrDataSource extends JsonAbstractAuditableModel implements DeletableObject {

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

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDataSourceTypeKey() {
		return dataSourceTypeKey;
	}

	public void setDataSourceTypeKey(String dataSourceTypeKey) {
		this.dataSourceTypeKey = dataSourceTypeKey;
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getDataSourceDescription() {
		return dataSourceDescription;
	}

	public void setDataSourceDescription(String dataSourceDescription) {
		this.dataSourceDescription = dataSourceDescription;
	}

	public String getDataSourceComment() {
		return dataSourceComment;
	}

	public void setDataSourceComment(String dataSourceComment) {
		this.dataSourceComment = dataSourceComment;
	}

	public String getDataSourceIp() {
		return dataSourceIp;
	}

	public void setDataSourceIp(String dataSourceIp) {
		this.dataSourceIp = dataSourceIp;
	}

	public String getDataSourcePort() {
		return dataSourcePort;
	}

	public void setDataSourcePort(String dataSourcePort) {
		this.dataSourcePort = dataSourcePort;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public Boolean get_isAnotherSubscriber() {
		return _isAnotherSubscriber;
	}

	public void set_isAnotherSubscriber(Boolean _isAnotherSubscriber) {
		this._isAnotherSubscriber = _isAnotherSubscriber;
	}

	public Integer getRawTimeout() {
		return rawTimeout;
	}

	public void setRawTimeout(Integer rawTimeout) {
		this.rawTimeout = rawTimeout;
	}

	public Integer getRawSleepTime() {
		return rawSleepTime;
	}

	public void setRawSleepTime(Integer rawSleepTime) {
		this.rawSleepTime = rawSleepTime;
	}

	public Integer getRawResendAttempts() {
		return rawResendAttempts;
	}

	public void setRawResendAttempts(Integer rawResendAttempts) {
		this.rawResendAttempts = rawResendAttempts;
	}

	public Integer getRawReconnectAttempts() {
		return rawReconnectAttempts;
	}

	public void setRawReconnectAttempts(Integer rawReconnectAttempts) {
		this.rawReconnectAttempts = rawReconnectAttempts;
	}

	public Integer getRawReconnectTimeout() {
		return rawReconnectTimeout;
	}

	public void setRawReconnectTimeout(Integer rawReconnectTimeout) {
		this.rawReconnectTimeout = rawReconnectTimeout;
	}

	public String getRawConnectionType() {
		return rawConnectionType;
	}

	public void setRawConnectionType(String rawConnectionType) {
		this.rawConnectionType = rawConnectionType;
	}

	public String getRawModemMacAddr() {
		return rawModemMacAddr;
	}

	public void setRawModemMacAddr(String rawModemMacAddr) {
		this.rawModemMacAddr = rawModemMacAddr;
	}

	public Long getRawModemModelId() {
		return rawModemModelId;
	}

	public void setRawModemModelId(Long rawModemModelId) {
		this.rawModemModelId = rawModemModelId;
	}

	public String getRawModemSerial() {
		return rawModemSerial;
	}

	public void setRawModemSerial(String rawModemSerial) {
		this.rawModemSerial = rawModemSerial;
	}

	public Boolean getRawModemDialEnable() {
		return rawModemDialEnable;
	}

	public void setRawModemDialEnable(Boolean rawModemDialEnable) {
		this.rawModemDialEnable = rawModemDialEnable;
	}

	public String getRawModemDialTel() {
		return rawModemDialTel;
	}

	public void setRawModemDialTel(String rawModemDialTel) {
		this.rawModemDialTel = rawModemDialTel;
	}

	public String getRawModemImei() {
		return rawModemImei;
	}

	public void setRawModemImei(String rawModemImei) {
		this.rawModemImei = rawModemImei;
	}

}
