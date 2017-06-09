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
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class DeviceModel extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, ExLabelObject, DevModeObject, DeletableObjectId,
    PersistableBuilder<DeviceModel, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = -6370569022830583056L;

	@Column(name = "device_model_name")
	private String modelName;

	@Column(name = "keyname")
	private String keyname;

	@Column(name = "caption")
	private String caption;

	@Column(name = "ex_code")
	private String exCode;

	@Column(name = "ex_label")
	private String exLabel;

	@Column(name = "ex_system", insertable = false, updatable = false)
	private String exSystem;

    @JsonIgnore
	@Column(name = "ex_system")
	private String exSystemKeyname;

	@Version
	private int version;

	@Column(name = "is_dev_mode")
	private Boolean isDevMode;

	@Column(name = "deleted")
	private int deleted;

	@Column(name = "meta_version")
	private Integer metaVersion = 1;

	@Column(name = "is_impulse")
	private Boolean isImpulse;

	@Column(name = "default_impulse_k")
	private BigDecimal defaultImpulseK;

	@Column(name = "default_impulse_mu")
	private String defaultImpulseMu;

	@Column(name = "device_type")
	private String deviceType;

	@ElementCollection
	@CollectionTable(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_data_type",
			joinColumns = @JoinColumn(name = "device_model_id"))
	@Column(name = "device_data_type")
	//@Fetch(value = FetchMode.SUBSELECT)
	private Set<String> deviceDataTypes = new HashSet<>();


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
