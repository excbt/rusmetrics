package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;
import ru.excbt.datafuse.nmk.data.model.markers.ExCodeObject;
import ru.excbt.datafuse.nmk.data.model.markers.ExSystemObject;
import ru.excbt.datafuse.nmk.data.model.markers.ManualObject;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;

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
public class ContObject extends AbstractAuditableModel
		implements ExSystemObject, ExCodeObject, DeletableObjectId, ManualObject {

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

	@Column(name = "cont_object_cw_temp")
	private BigDecimal cwTemp;

	@Column(name = "cont_object_heat_area")
	private BigDecimal heatArea;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerContacts() {
		return ownerContacts;
	}

	public void setOwnerContacts(String ownerContacts) {
		this.ownerContacts = ownerContacts;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCurrentSettingMode() {
		return currentSettingMode;
	}

	public void setCurrentSettingMode(String currentSettingMode) {
		this.currentSettingMode = currentSettingMode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getCwTemp() {
		return cwTemp;
	}

	public void setCwTemp(BigDecimal cwTemp) {
		this.cwTemp = cwTemp;
	}

	public BigDecimal getHeatArea() {
		return heatArea;
	}

	public void setHeatArea(BigDecimal heatArea) {
		this.heatArea = heatArea;
	}

	public List<ContManagement> getContManagements() {
		return contManagements;
	}

	public void setContManagements(List<ContManagement> contManagements) {
		this.contManagements = contManagements;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getSettingModeMDate() {
		return settingModeMDate;
	}

	public void setSettingModeMDate(Date settingModeMDate) {
		this.settingModeMDate = settingModeMDate;
	}

	@Override
	public String getExSystemKeyname() {
		return exSystemKeyname;
	}

	@Override
	public String getExCode() {
		return exCode;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Override
	public Boolean getIsManual() {
		return isManual;
	}

	public void setIsManual(Boolean isManual) {
		this.isManual = isManual;
	}

	public String getTimezoneDefKeyname() {
		return timezoneDefKeyname;
	}

	public void setTimezoneDefKeyname(String timezoneDefKeyname) {
		this.timezoneDefKeyname = timezoneDefKeyname;
	}

	public void setExSystemKeyname(String exSystemKeyname) {
		this.exSystemKeyname = exSystemKeyname;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public Boolean get_haveSubscr() {
		return _haveSubscr;
	}

	public void set_haveSubscr(Boolean _haveSubscr) {
		this._haveSubscr = _haveSubscr;
	}

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
	public String get_daDataSraw() {
		return _daDataSraw;
	}

	/**
	 * 
	 * @param _daDataSraw
	 */
	public void set_daDataSraw(String _daDataSraw) {
		this._daDataSraw = _daDataSraw;
	}

	public Boolean getIsAddressAuto() {
		return isAddressAuto;
	}

	public void setIsAddressAuto(Boolean isAddressAuto) {
		this.isAddressAuto = isAddressAuto;
	}

	public Boolean getIsValidFiasUUID() {
		return isValidFiasUUID;
	}

	public void setIsValidFiasUUID(Boolean isValidFiasUUID) {
		this.isValidFiasUUID = isValidFiasUUID;
	}

	public Boolean getIsValidGeoPos() {
		return isValidGeoPos;
	}

	public void setIsValidGeoPos(Boolean isValidGeoPos) {
		this.isValidGeoPos = isValidGeoPos;
	}

	public String getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	public String getBuildingTypeCategory() {
		return buildingTypeCategory;
	}

	public void setBuildingTypeCategory(String buildingTypeCategory) {
		this.buildingTypeCategory = buildingTypeCategory;
	}

	public Integer getNumOfStories() {
		return numOfStories;
	}

	public void setNumOfStories(Integer numOfStories) {
		this.numOfStories = numOfStories;
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

}
