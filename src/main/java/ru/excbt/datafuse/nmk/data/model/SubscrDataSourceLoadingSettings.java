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
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_data_source_loading_settings")
@Getter
@Setter
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

	@Column(name = "nodata_request_enabled")
	private Boolean nodataRequestEnabled;

	@Column(name = "nodata_idle_time")
	private String nodataIdleTime;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	@Transient
	private String dataSourceName;

}
