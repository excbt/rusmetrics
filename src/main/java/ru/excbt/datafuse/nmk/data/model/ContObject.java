package ru.excbt.datafuse.nmk.data.model;

import org.hibernate.annotations.*;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.PersistableBuilder;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;
import ru.excbt.datafuse.nmk.data.model.markers.ManualObject;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * Контейнер учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.02.2015
 *
 */
@Entity
@Table(name = "cont_object")
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ContObject extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, DeletableObjectId, ManualObject, PersistableBuilder<ContObject,Long> {

	private static final Logger logger = LoggerFactory.getLogger(ContObject.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 2174907606605476227L;

	/**
	 *
	 */

	@Column(name = "cont_object_name")
	private String name;

	@Column(name = "cont_object_full_name")
	private String fullName;

	@Column(name = "cont_object_full_address")
	private String fullAddress;

	@Column(name = "cont_object_number")
	private String number;

	@Column(name = "cont_object_owner")
	private String owner;

	@Column(name = "owner_contacts")
	private String ownerContacts;

	@Column(name = "current_setting_mode")
	private String currentSettingMode;

	@Column(name = "setting_mode_mdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date settingModeMDate;

	@Column(name = "cont_object_description")
	private String description;

	@JsonIgnore
	@Column(name = "cont_object_comment")
	private String comment;

	@Column(name = "cont_object_cw_temp", columnDefinition = "numeric")
	private Double cwTemp;

	@Column(name = "cont_object_heat_area", columnDefinition = "numeric")
	private Double heatArea;

	@Version
	private int version;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "contObject")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<ContManagement> contManagements = new ArrayList<>();

	@Column(name = "timezone_def")
	private String timezoneDefKeyname;

	@Column(name = "ex_system")
	private String exSystemKeyname;

	@Column(name = "ex_code")
	@JsonIgnore
	private String exCode;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "is_manual")
	private Boolean isManual;

	@Transient
	private Boolean _haveSubscr;

	@Transient
	private String _daDataSraw;

	@Column(name = "is_address_auto")
	private Boolean isAddressAuto;

	@Column(name = "is_valid_fias_uuid")
	private Boolean isValidFiasUUID;

	@Column(name = "is_valid_geo_pos")
	private Boolean isValidGeoPos;

	@Column(name = "building_type")
	private String buildingType;

	@Column(name = "building_type_category")
	private String buildingTypeCategory;

	@Column(name = "num_of_storeys")
	private Integer numOfStories;

	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = DBMetadata.SCHEME_PORTAL,  name="cont_object_meter_period", joinColumns=@JoinColumn(name="cont_object_id"),
    	inverseJoinColumns=@JoinColumn(name="meter_period_setting_id"))
    @MapKeyColumn(name = "cont_service_type")
    private Map<String, MeterPeriodSetting> meterPeriodSettings = new HashMap<>();


    @Column(name = "flex_data")
    @Type(type = "JsonbAsString")
    private String flexData;

	/**
	 *
	 * @return
	 */
	public ContManagement get_activeContManagement() {
		ContManagement result = null;
		if (contManagements != null && contManagements.size() > 0) {

			List<ContManagement> actimeCM = contManagements.stream().filter(i -> i.getEndDate() == null)
					.sorted((a, b) -> Long.compare(b.getId(), a.getId())).collect(Collectors.toList());

			if (actimeCM.size() > 1) {
				logger.error("ContObject (id=%d) has more than one active ContManagement", getId());
			}

			if (!actimeCM.isEmpty()) {
				result = actimeCM.get(0);
			}
		}
		return result;
	}


	/**
	 *
	 * @return
	 */
	@JsonIgnore
	public ContObjectShortInfo getContObjectShortInfo() {
		return new ContObjectShortInfo() {

			@Override
			public String getFullName() {
				return fullName;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Long getContObjectId() {
				return getId();
			}

		};
	}


	public void updateFromContObject (ContObject contObject) {

        setVersion(contObject.getVersion());
        setName(contObject.getName());
        setFullName(contObject.getFullName());
        setFullAddress(contObject.getFullAddress());
        setNumber(contObject.getNumber());
        setDescription(contObject.getDescription());
        setCurrentSettingMode(contObject.getCurrentSettingMode());
        setComment(contObject.getComment());
        setOwner(contObject.getOwner());
        setOwnerContacts(contObject.getOwnerContacts());
        setCwTemp(contObject.getCwTemp());
        setHeatArea(contObject.getHeatArea());
        setTimezoneDefKeyname(contObject.getTimezoneDefKeyname());
        setBuildingType(contObject.getBuildingType());
        setBuildingTypeCategory(contObject.getBuildingTypeCategory());
        setNumOfStories(contObject.getNumOfStories());
    }

    /**
     *
     * @param contObjectDTO
     */
	public void updateFromContObjectDTO (ContObjectDTO contObjectDTO) {
        setVersion(contObjectDTO.getVersion());
        setName(contObjectDTO.getName());
        setFullName(contObjectDTO.getFullName());
        setFullAddress(contObjectDTO.getFullAddress());
        setNumber(contObjectDTO.getNumber());
        setDescription(contObjectDTO.getDescription());
        setCurrentSettingMode(contObjectDTO.getCurrentSettingMode());
        setComment(contObjectDTO.getComment());
        setOwner(contObjectDTO.getOwner());
        setOwnerContacts(contObjectDTO.getOwnerContacts());
        setCwTemp(contObjectDTO.getCwTemp());
        setHeatArea(contObjectDTO.getHeatArea());
        setTimezoneDefKeyname(contObjectDTO.getTimezoneDefKeyname());
        setBuildingType(contObjectDTO.getBuildingType());
        setBuildingTypeCategory(contObjectDTO.getBuildingTypeCategory());
        setNumOfStories(contObjectDTO.getNumOfStories());
        setFlexData(contObjectDTO.getFlexData());
    }

    public boolean haveDaData() {
	    return _daDataSraw != null && _daDataSraw != "";
    }

}
