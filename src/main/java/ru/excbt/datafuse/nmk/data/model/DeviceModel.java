package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExLabelObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;

/**
 * Модель прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2015
 *
 */
@Entity
@Table(name = "device_model")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeviceModel extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, ExLabelObject, DevModeObject, DeletableObjectId,
    PersistableBuilder<DeviceModel, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = -6370569022830583056L;

	@Column(name = "device_model_name")
    @Getter
    @Setter
	private String modelName;

	@Column(name = "keyname")
    @Getter
    @Setter
	private String keyname;

	@Column(name = "caption")
    @Getter
    @Setter
	private String caption;

	@Column(name = "ex_code")
    @Getter
    @Setter
	private String exCode;

	@Column(name = "ex_label")
    @Getter
    @Setter
	private String exLabel;

	@Column(name = "ex_system", insertable = false, updatable = false)
    @Getter
    @Setter
	private String exSystem;

    @JsonIgnore
	@Column(name = "ex_system")
    @Getter
    @Setter
	private String exSystemKeyname;

	@Version
    @Getter
    @Setter
	private int version;

	@Column(name = "is_dev_mode")
    @Getter
    @Setter
	private Boolean isDevMode;

	@Column(name = "deleted")
    @Getter
    @Setter
	private int deleted;

	@Column(name = "meta_version")
    @Getter
    @Setter
	private Integer metaVersion = 1;

	@Column(name = "is_impulse")
    @Getter
    @Setter
	private Boolean isImpulse;

	@Column(name = "default_impulse_k")
    @Getter
    @Setter
	private BigDecimal defaultImpulseK;

	@Column(name = "default_impulse_mu")
    @Getter
    @Setter
	private String defaultImpulseMu;

	@Column(name = "device_type")
    @Getter
    @Setter
	private String deviceType;

	@ElementCollection
	@CollectionTable(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_type_group",
			joinColumns = @JoinColumn(name = "device_model_id"))
	@Column(name = "device_model_type")
	//@Fetch(value = FetchMode.SUBSELECT)
    @Getter
    @Setter
	private Set<String> deviceModelTypes = new HashSet<>();


//    @ElementCollection
//    @CollectionTable(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_heat_radiator", joinColumns=@JoinColumn(name="device_model_id"))
//    //@MapKeyColumn(name = "heat_radiator_id")
//    @MapKeyJoinColumn(name = "heat_radiator_type_id")
//    @Column(name = "kc", columnDefinition = "numeric(12,4)", precision = 12, scale = 4 )
//    @Fetch(value = FetchMode.SUBSELECT)
//    @Getter
//    @Setter
//    private Map<HeatRadiatorType,Double> heatRadiatorKcs = new HashMap<>();



}
