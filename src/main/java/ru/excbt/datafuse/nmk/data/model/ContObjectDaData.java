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

import lombok.Getter;
import lombok.Setter;
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
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
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
	@Type(type = "JsonbAsString")
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

	@Column(name = "data_fias_id", columnDefinition = "uuid")
	private UUID dataFiasId;

	@Column(name = "data_geo_lat", columnDefinition = "numeric")
	private Double dataGeoLat;

	@Column(name = "data_geo_lon", columnDefinition = "numeric")
	private Double dataGeoLon;

	@Column(name = "is_valid")
	private Boolean isValid;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;


	public void clearInValid() {
        sraw = null;
        dataGeoLat = null;
        dataGeoLon = null;
        dataFiasId = null;
        isValid = false;
    }

    private static final String GEO_POS_JSON_TEMPLATE = "{\"pos\": \"%s %s\"}";

    public String makeJsonGeoString() {
        if (dataGeoLat == null || dataGeoLon == null) {
            return null;
        }
        return String.format(GEO_POS_JSON_TEMPLATE, dataGeoLon.toString(),
            dataGeoLat.toString());
    }

    @JsonIgnore
    public boolean isAddressAuto() {
        return Boolean.TRUE.equals(isValid);
    }

}
