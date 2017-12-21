package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Источник данных прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_object_data_source2")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class DeviceObjectDataSource2 implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9218504365025332432L;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_object_id")
    private DeviceObject deviceObject;

//    @Column(name = "device_object_id", insertable = false, updatable = false)
//    private Long deviceObjectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscr_data_source_id")
    private SubscrDataSource subscrDataSource;

    @Column(name = "subscr_data_source_addr")
    private String subscrDataSourceAddr;

    @Column(name = "data_source_table")
    private String dataSourceTable;

    @Column(name = "data_source_table_1h")
    private String dataSourceTable1h;

    @Column(name = "data_source_table_24h")
    private String dataSourceTable24h;

    @Version
    @Column(name = "version")
    private int version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceObjectDataSource2 that = (DeviceObjectDataSource2) o;
        return Objects.equals(deviceObject, that.deviceObject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(deviceObject);
    }
}
