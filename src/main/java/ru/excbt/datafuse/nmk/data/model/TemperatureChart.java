package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.LocalPlace.LocalPlaceInfo;
import ru.excbt.datafuse.nmk.data.model.Organization.OrganizationInfo;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TemperatureChart extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -3468789788450905535L;

	@Column(name = "local_place_id", updatable = false, insertable = false)
	private Long localPlaceId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "local_place_id")
	private LocalPlace localPlace;

	@Column(name = "rso_organization_id", updatable = false, insertable = false)
	private Long rsoOrganizationId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rso_organization_id")
	private Organization rsoOrganization;

	@Column(name = "chart_name")
	private String chartName;

	@Column(name = "chart_description")
	private String chartDescaription;

	@Column(name = "chart_comment")
	private String chartComment;

	@Column(name = "chart_deviation_value")
	private BigDecimal chartDeviationValue;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "max_t")
	private BigDecimal maxT;

	@Column(name = "min_t")
	private BigDecimal minT;

	@Column(name = "flag_adj_pump")
	private Boolean flagAdjPump;

	@Column(name = "flag_elevator")
	private Boolean flagElevator;

	@Column(name = "is_ok")
	private Boolean isOk;

	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Transient
	private OrganizationInfo rsoOrganizationInfo;

	@Transient
	private LocalPlaceInfo localPlaceInfo;

	public Long getLocalPlaceId() {
		return localPlaceId;
	}

	public void setLocalPlaceId(Long localPlaceId) {
		this.localPlaceId = localPlaceId;
	}

	public Long getRsoOrganizationId() {
		return rsoOrganizationId;
	}

	public void setRsoOrganizationId(Long rsoOrganizationId) {
		this.rsoOrganizationId = rsoOrganizationId;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public BigDecimal getMaxT() {
		return maxT;
	}

	public void setMaxT(BigDecimal maxT) {
		this.maxT = maxT;
	}

	public BigDecimal getMinT() {
		return minT;
	}

	public void setMinT(BigDecimal minT) {
		this.minT = minT;
	}

	public Boolean getFlagAdjPump() {
		return flagAdjPump;
	}

	public void setFlagAdjPump(Boolean flagAdjPump) {
		this.flagAdjPump = flagAdjPump;
	}

	public Boolean getFlagElevator() {
		return flagElevator;
	}

	public void setFlagElevator(Boolean flagElevator) {
		this.flagElevator = flagElevator;
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

	public LocalPlace getLocalPlace() {
		return localPlace;
	}

	public void setLocalPlace(LocalPlace localPlace) {
		this.localPlace = localPlace;
	}

	public Organization getRsoOrganization() {
		return rsoOrganization;
	}

	public void setRsoOrganization(Organization rsoOrganization) {
		this.rsoOrganization = rsoOrganization;
	}

	public String getChartDescaription() {
		return chartDescaription;
	}

	public void setChartDescaription(String chartDescaription) {
		this.chartDescaription = chartDescaription;
	}

	public String getChartComment() {
		return chartComment;
	}

	public void setChartComment(String chartComment) {
		this.chartComment = chartComment;
	}

	public Boolean getIsOk() {
		return isOk;
	}

	public void setIsOk(Boolean isOk) {
		this.isOk = isOk;
	}

	public LocalPlaceInfo getLocalPlaceInfo() {
		return localPlaceInfo;
	}

	public OrganizationInfo getRsoOrganizationInfo() {
		return rsoOrganizationInfo;
	}

	public void initLocalPlaceInfo() {
		LocalPlaceInfo lpi = new LocalPlaceInfo(this.getLocalPlace());
		this.localPlaceInfo = lpi;
	}

	public void initRsoOrganizationInfo() {
		OrganizationInfo oi = new OrganizationInfo(this.getRsoOrganization());
		this.rsoOrganizationInfo = oi;
	}

	public BigDecimal getChartDeviationValue() {
		return chartDeviationValue;
	}

	public void setChartDeviationValue(BigDecimal chartDeviationValue) {
		this.chartDeviationValue = chartDeviationValue;
	}

}
