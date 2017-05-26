package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kovtonyk on 12.04.2017.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyPassportDataId implements Serializable {

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "passport_id")
    private EnergyPassport passport;

    @NotNull
    @Column(name = "section_key")
    private String sectionKey;

    @Column(name = "section_entry_id")
    private Long sectionEntryId;

    @NotNull
    @Column(name = "complex_idx")
    private String complexIdx;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyPassportDataId that = (EnergyPassportDataId) o;

        if (!passport.equals(that.passport)) return false;
        if (!sectionKey.equals(that.sectionKey)) return false;
        return complexIdx.equals(that.complexIdx);
    }

    @Override
    public int hashCode() {
        int result = passport.hashCode();
        result = 31 * result + sectionKey.hashCode();
        result = 31 * result + complexIdx.hashCode();
        return result;
    }
}
