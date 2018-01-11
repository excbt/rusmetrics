package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Подписка контейнера на ресурсные системы
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "cont_zpoint")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ContZPoint extends AbstractAuditableModel implements ExSystemObject, ExCodeObject, DeletableObjectId,
    PersistableBuilder<ContZPoint, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "cont_object_id", updatable = false)
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@ManyToOne
	@JoinColumn(name = "cont_service_type")
	private ContServiceType contServiceType;

	@Column(name = "cont_service_type", updatable = false, insertable = false)
	private String contServiceTypeKeyname;

	@Column(name = "custom_service_name")
	private String customServiceName;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "cont_zpoint_device", joinColumns = @JoinColumn(name = "cont_zpoint_id"),
			inverseJoinColumns = @JoinColumn(name = "device_object_id"))
	private DeviceObject deviceObject;

	@Version
	private int version;

	@ManyToOne()
	@JoinColumn(name = "rso_organization_id")
	private Organization rso;

	@Column(name = "rso_organization_id", updatable = false, insertable = false)
	private Long rsoId;

	@Column(name = "checkout_time")
	private String checkoutTime;

	@Column(name = "checkout_day")
	private Integer checkoutDay;

	@Column(name = "is_double_pipe")
	private Boolean doublePipe;

	@Column(name = "is_manual_loading")
	private Boolean isManualLoading;

	@Column(name = "ex_system")
	private String exSystemKeyname;

	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ts_number")
	private Integer tsNumber;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_manual")
	private Boolean isManual;

	@Column(name = "cont_zpoint_comment")
	private String contZPointComment;

	@Column(name = "is_drools_disable")
	private Boolean isDroolsDisable;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "temperature_chart_id")
	private TemperatureChart temperatureChart;

	@Column(name = "temperature_chart_id", insertable = false, updatable = false)
	private Long temperatureChartId;

    @Column(name = "flex_data")
    @Type(type = "JsonbAsString")
	private String flexData;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contZPoint")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ContZPointConsField> consFields = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_cons",
//        joinColumns = @JoinColumn(name = "cont_zpoint_id"))
//    @Column(name = "field_name")
//    //@Fetch(value = FetchMode.SUBSELECT)
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//    private Set<String> consumptionFields = new HashSet<>();
//
    @Override
    public String toString() {
        return "ContZPoint{" +
            "contObjectId=" + contObjectId +
            ", contServiceType=" + contServiceType +
            ", contServiceTypeKeyname='" + contServiceTypeKeyname + '\'' +
            ", customServiceName='" + customServiceName + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", deviceObject=" + deviceObject +
            ", version=" + version +
            ", rso=" + rso +
            ", rsoId=" + rsoId +
            ", checkoutTime='" + checkoutTime + '\'' +
            ", checkoutDay=" + checkoutDay +
            ", doublePipe=" + doublePipe +
            ", isManualLoading=" + isManualLoading +
            ", exSystemKeyname='" + exSystemKeyname + '\'' +
            ", exCode='" + exCode + '\'' +
            ", tsNumber=" + tsNumber +
            ", deleted=" + deleted +
            ", isManual=" + isManual +
            ", contZPointComment='" + contZPointComment + '\'' +
            ", isDroolsDisable=" + isDroolsDisable +
            ", temperatureChart=" + temperatureChart +
            ", temperatureChartId=" + temperatureChartId +
            ", flexData='" + flexData + '\'' +
            "} " + super.toString();
    }
}
