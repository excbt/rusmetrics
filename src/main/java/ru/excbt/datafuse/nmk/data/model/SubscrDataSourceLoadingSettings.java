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

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_data_source_loading_settings")
public class SubscrDataSourceLoadingSettings extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3159933589431834792L;

	@Column(name = "subscr_data_source_id", insertable = false, updatable = false)
	private Long subscrDataSourceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_data_source_id")
	private SubscrDataSource subscrDataSource;

	@Column(name = "is_loading_auto")
	private Boolean isLoadingAuto;

	@Column(name = "loading_interval")
	private String loadingInterval;

	@Column(name = "loading_attempts")
	private Integer loadingAttempts;

	@Column(name = "loading_retry_interval")
	private String loadingRetryInterval;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Transient
	private String dataSourceName;

	public Long getSubscrDataSourceId() {
		return subscrDataSourceId;
	}

	public void setSubscrDataSourceId(Long subscrDataSourceId) {
		this.subscrDataSourceId = subscrDataSourceId;
	}

	public SubscrDataSource getSubscrDataSource() {
		return subscrDataSource;
	}

	public void setSubscrDataSource(SubscrDataSource subscrDataSource) {
		this.subscrDataSource = subscrDataSource;
	}

	public Boolean getIsLoadingAuto() {
		return isLoadingAuto;
	}

	public void setIsLoadingAuto(Boolean isLoadingAuto) {
		this.isLoadingAuto = isLoadingAuto;
	}

	public String getLoadingInterval() {
		return loadingInterval;
	}

	public void setLoadingInterval(String loadingInterval) {
		this.loadingInterval = loadingInterval;
	}

	public Integer getLoadingAttempts() {
		return loadingAttempts;
	}

	public void setLoadingAttempts(Integer loadingAttempts) {
		this.loadingAttempts = loadingAttempts;
	}

	public String getLoadingRetryInterval() {
		return loadingRetryInterval;
	}

	public void setLoadingRetryInterval(String loadingRetryInterval) {
		this.loadingRetryInterval = loadingRetryInterval;
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

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

}
