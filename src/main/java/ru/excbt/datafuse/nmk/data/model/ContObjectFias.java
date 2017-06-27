package ru.excbt.datafuse.nmk.data.model;

import java.util.UUID;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * Контейнер учета - Фиас и гео-информация
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 */
@Entity
@Table(name = "cont_object_fias")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ContObjectFias extends AbstractAuditableModel implements DeletableObject {

    /**
     *
     */
    private static final long serialVersionUID = 4834456607858555535L;

    @OneToOne
    @JoinColumn(name = "cont_object_id")
    @JsonIgnore
    private ContObject contObject;

    //@Column(name = "cont_object_id", insertable = false, updatable = false)
    //@Column(name = "cont_object_id")
    //private Long contObjectId;

    @Column(name = "fias_uuid")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID fiasUUID;

    @Column(name = "fias_full_address")
    @JsonIgnore
    private String fiasFullAddress;

    @Column(name = "city_fias_uuid")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID cityFiasUUID;

    @Column(name = "geo_full_address")
    private String geoFullAddress;

    @Column(name = "short_address_1")
    private String shortAddress1;

    @Column(name = "short_address_2")
    private String shortAddress2;

    @Column(name = "short_address_3")
    private String shortAddress3;

    @JsonIgnore
    @Column(name = "geo_json")
    @Type(type = "StringJsonObject")
    private String geoJson;

    @JsonIgnore
    @Column(name = "geo_json_2")
    @Type(type = "StringJsonObject")
    private String geoJson2;

    @JsonIgnore
    @Version
    private int version;

    @JsonIgnore
    @Column(name = "is_geo_refresh")
    private Boolean isGeoRefresh;

    @JsonIgnore
    @Column(name = "deleted")
    private int deleted;

    public void copyFormDaData(ContObjectDaData contObjectDaData) {
        fiasFullAddress = contObjectDaData.getSvalue();
        geoFullAddress = contObjectDaData.getSvalue();
        fiasUUID = contObjectDaData.getDataFiasId();
        isGeoRefresh = contObjectDaData.getSvalue() != null;
        geoJson2 = contObjectDaData.makeJsonGeoString();
        isGeoRefresh = true;
    }

    public void clearCodes() {
        setFiasUUID(null);
        setCityFiasUUID(null);
        setGeoJson(null);
        setGeoJson2(null);
        setIsGeoRefresh(true);
    }

    public Long getContObjectId() {
        return contObject != null ? contObject.getId() : null;
    }

}
