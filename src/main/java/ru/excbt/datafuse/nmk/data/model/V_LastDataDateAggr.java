package ru.excbt.datafuse.nmk.data.model;

import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Subselect("select cont_zpoint_id, last_data_date, last_data_date_time, data_type, mv_refresh_date " +
    " from portal.mv_last_data_date_aggr")
public class V_LastDataDateAggr implements Serializable {

    @Id
    @Column(name = "cont_zpoint_id", updatable = false, insertable = false)
    private Long contZPointId;

    @Column(name = "last_data_date")
    private LocalDateTime lastDataDate;

    public Long getContZPointId() {
        return contZPointId;
    }

    public void setContZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
    }

    public LocalDateTime getLastDataDate() {
        return lastDataDate;
    }

    public void setLastDataDate(LocalDateTime lastDataDate) {
        this.lastDataDate = lastDataDate;
    }
}
