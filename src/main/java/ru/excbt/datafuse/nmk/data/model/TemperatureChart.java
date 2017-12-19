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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.LocalPlace.LocalPlaceInfo;
import ru.excbt.datafuse.nmk.data.model.Organization.OrganizationInfo;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "temperature_chart")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class TemperatureChart extends AbstractAuditableModel implements DeletableObjectId, PersistableBuilder<TemperatureChart, Long> {

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

	@Column(name = "chart_deviation_value", columnDefinition = "numeric")
	private Double chartDeviationValue;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "max_t", columnDefinition = "numeric")
	private Double maxT;

	@Column(name = "min_t", columnDefinition = "numeric")
	private Double minT;

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


	public void initLocalPlaceInfo() {
		LocalPlaceInfo lpi = new LocalPlaceInfo(this.getLocalPlace());
		this.localPlaceInfo = lpi;
	}

	public void initRsoOrganizationInfo() {
		OrganizationInfo oi = new OrganizationInfo(this.getRsoOrganization());
		this.rsoOrganizationInfo = oi;
	}

}
