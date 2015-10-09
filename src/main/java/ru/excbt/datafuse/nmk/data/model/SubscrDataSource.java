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

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

@Entity
@Table(name = "subscr_data_source")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class SubscrDataSource extends AbstractAuditableModel implements DeletableObject {

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
	@JsonIgnore
	private String dbPassword;

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

}
