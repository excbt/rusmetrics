package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObjectId;

/**
 * Данные по адресу для объекта учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.01.2016
 *
 */
@Entity
@Table(name = "cont_object_dadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContObjectDaData extends AbstractAuditableModel implements DeletableObjectId {

	/**
	 *
	 */
	private static final long serialVersionUID = -4366148412088419088L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@Column(name = "s_raw")
	@Type(type = "StringJsonObject")
	private String sraw;

	@Column(name = "s_value")
	private String svalue;

	@Column(name = "data_region_type_full")
	private String dataRegionTypeFull;

	@Column(name = "data_region")
	private String dataRegion;

	@Column(name = "data_area_type_full")
	private String dataAreaTypeFull;

	@Column(name = "data_area")
	private String dataArea;

	@Column(name = "data_city_type_full")
	private String dataCityTypeFull;

	@Column(name = "data_city")
	private String dataCity;

	@Column(name = "data_settlement_type_full")
	private String dataSettlementTypeFull;

	@Column(name = "data_settlement")
	private String dataSettlement;

	@Column(name = "data_city_district")
	private String dataCityDistrict;

	@Column(name = "data_street_type_full")
	private String dataStreetTypeFull;

	@Column(name = "data_street")
	private String dataStreet;

	@Column(name = "data_house_type_full")
	private String dataHouseTypeFull;

	@Column(name = "data_house")
	private String dataHouse;

	@Column(name = "data_block_type_full")
	private String dataBlockTypeFull;

	@Column(name = "data_block")
	private String dataBlock;

	@Column(name = "data_flat_type_full")
	private String dataFlatTypeFull;

	@Column(name = "data_flat")
	private String dataFlat;

	@Column(name = "data_fias_id")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID dataFiasId;

	@Column(name = "data_geo_lat")
	private BigDecimal dataGeoLat;

	@Column(name = "data_geo_lon")
	private BigDecimal dataGeoLon;

	@Column(name = "is_valid")
	private Boolean isValid;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public String getSraw() {
		return sraw;
	}

	public void setSraw(String sraw) {
		this.sraw = sraw;
	}

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	public String getDataRegion() {
		return dataRegion;
	}

	public void setDataRegion(String dataRegion) {
		this.dataRegion = dataRegion;
	}

	public String getDataAreaTypeFull() {
		return dataAreaTypeFull;
	}

	public void setDataAreaTypeFull(String dataAreaTypeFull) {
		this.dataAreaTypeFull = dataAreaTypeFull;
	}

	public String getDataArea() {
		return dataArea;
	}

	public void setDataArea(String dataArea) {
		this.dataArea = dataArea;
	}

	public String getDataCityTypeFull() {
		return dataCityTypeFull;
	}

	public void setDataCityTypeFull(String dataCityTypeFull) {
		this.dataCityTypeFull = dataCityTypeFull;
	}

	public String getDataCity() {
		return dataCity;
	}

	public void setDataCity(String dataCity) {
		this.dataCity = dataCity;
	}

	public String getDataSettlementTypeFull() {
		return dataSettlementTypeFull;
	}

	public void setDataSettlementTypeFull(String dataSettlementTypeFull) {
		this.dataSettlementTypeFull = dataSettlementTypeFull;
	}

	public String getDataSettlement() {
		return dataSettlement;
	}

	public void setDataSettlement(String dataSettlement) {
		this.dataSettlement = dataSettlement;
	}

	public String getDataCityDistrict() {
		return dataCityDistrict;
	}

	public void setDataCityDistrict(String dataCityDistrict) {
		this.dataCityDistrict = dataCityDistrict;
	}

	public String getDataStreetTypeFull() {
		return dataStreetTypeFull;
	}

	public void setDataStreetTypeFull(String dataStreetTypeFull) {
		this.dataStreetTypeFull = dataStreetTypeFull;
	}

	public String getDataStreet() {
		return dataStreet;
	}

	public void setDataStreet(String dataStreet) {
		this.dataStreet = dataStreet;
	}

	public String getDataHouseTypeFull() {
		return dataHouseTypeFull;
	}

	public void setDataHouseTypeFull(String dataHouseTypeFull) {
		this.dataHouseTypeFull = dataHouseTypeFull;
	}

	public String getDataHouse() {
		return dataHouse;
	}

	public void setDataHouse(String dataHouse) {
		this.dataHouse = dataHouse;
	}

	public String getDataBlockTypeFull() {
		return dataBlockTypeFull;
	}

	public void setDataBlockTypeFull(String dataBlockTypeFull) {
		this.dataBlockTypeFull = dataBlockTypeFull;
	}

	public String getDataBlock() {
		return dataBlock;
	}

	public void setDataBlock(String dataBlock) {
		this.dataBlock = dataBlock;
	}

	public String getDataFlatTypeFull() {
		return dataFlatTypeFull;
	}

	public void setDataFlatTypeFull(String dataFlatTypeFull) {
		this.dataFlatTypeFull = dataFlatTypeFull;
	}

	public String getDataFlat() {
		return dataFlat;
	}

	public void setDataFlat(String dataFlat) {
		this.dataFlat = dataFlat;
	}

	public UUID getDataFiasId() {
		return dataFiasId;
	}

	public void setDataFiasId(UUID dataFiasId) {
		this.dataFiasId = dataFiasId;
	}

	public BigDecimal getDataGeoLat() {
		return dataGeoLat;
	}

	public void setDataGeoLat(BigDecimal dataGeoLat) {
		this.dataGeoLat = dataGeoLat;
	}

	public BigDecimal getDataGeoLon() {
		return dataGeoLon;
	}

	public void setDataGeoLon(BigDecimal dataGeoLon) {
		this.dataGeoLon = dataGeoLon;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
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

	public String getDataRegionTypeFull() {
		return dataRegionTypeFull;
	}

	public void setDataRegionTypeFull(String dataRegionTypeFull) {
		this.dataRegionTypeFull = dataRegionTypeFull;
	}

}
