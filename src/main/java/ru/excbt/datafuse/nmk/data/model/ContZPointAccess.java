package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipEntryOutputStream;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_access")
@Getter
@Setter
@IdClass(ContZPointAccess.PK.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContZPointAccess {

    @Getter
    @Setter
    public static final class PK implements Serializable {

        @NotNull
        @Column(name = "subscriber_id")
        private Long subscriberId;

        @NotNull
        @Column(name = "cont_zpoint_id")
        private Long contZPointId;

        public PK subscriberId(Long subscriberId) {
            this.subscriberId = subscriberId;
            return this;
        }

        public PK contZPointId(Long contZPointId) {
            this.contZPointId = contZPointId;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PK sId = (PK) o;

            if (subscriberId != null ? !subscriberId.equals(sId.subscriberId) : sId.subscriberId != null) return false;
            return contZPointId != null ? contZPointId.equals(sId.contZPointId) : sId.contZPointId == null;
        }

        @Override
        public int hashCode() {
            int result = subscriberId != null ? subscriberId.hashCode() : 0;
            result = 31 * result + (contZPointId != null ? contZPointId.hashCode() : 0);
            return result;
        }
    }

    @Id
    @JoinColumn(name = "subscriber_id")
    private Long subscriberId;

    @Id
    @JoinColumn(name = "cont_zpoint_id")
    private Long contZPointId;

    @NotNull
    @Column(name = "grant_tz", updatable = false)
    private ZonedDateTime grantTZ = ZonedDateTime.now();

    @Column(name = "revoke_tz")
    private ZonedDateTime revokeTz;

    @Column(name = "access_type")
    private String accessType;

    @Column(name = "access_ttl")
    private LocalDateTime accessTtl;

    @Column(name = "access_ttl_tz")
    private ZonedDateTime accessTtlTz;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", insertable = false, updatable = false)
    private Subscriber subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cont_zpoint_id", insertable = false, updatable = false)
    private ContZPoint contZPoint;

    public ContZPointAccess subscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public ContZPointAccess contZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
        return this;
    }

    public void startNewAccess() {
        this.clearRevokeTZ();
        this.clearAccessTTL();
        this.accessType = null;
        this.grantTZ = ZonedDateTime.now();
    }

    public void clearAccessTTL(){
        this.accessTtl = null;
        this.accessTtlTz = null;
    }

    public void clearRevokeTZ(){
        this.revokeTz = null;
    }

}
