package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by kovtonyk on 08.06.2017.
 */
@Entity
@Table(schema = "gate", name = "cabinet_out_meter_data_q")
@Getter
@Setter
public class CabinetOutMeterDataQ {

    @Embeddable
    @Getter
    @Setter
    public static final class QId implements Serializable {

        private Long id;

        @Column(name = "q_datetime")
        private ZonedDateTime qDateTime = ZonedDateTime.now();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QId qId = (QId) o;

            if (id != null ? !id.equals(qId.id) : qId.id != null) return false;
            return qDateTime != null ? qDateTime.equals(qId.qDateTime) : qId.qDateTime == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (qDateTime != null ? qDateTime.hashCode() : 0);
            return result;
        }
    }

    @EmbeddedId
    private QId qId = new QId();

    public static CabinetOutMeterDataQ newQ (Long id) {
        CabinetOutMeterDataQ result = new CabinetOutMeterDataQ();
        result.getQId().setId(id);
        return result;
    }

}
