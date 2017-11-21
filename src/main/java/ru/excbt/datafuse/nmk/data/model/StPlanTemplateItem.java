package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "st_plan_template_item")
@IdClass(StPlanTemplateItem.PK.class)
public class StPlanTemplateItem  {


    @Getter
    @Setter
    public static final class PK implements Serializable {

        @Column(name = "template_keyname")
        @NotNull
        @Max(32)
        private String templateKeyname;

        @Column(name = "rn")
        @NotNull
        private Integer rn;


        public StPlanTemplateItem.PK templateKeyname(String templateKeyname) {
            this.templateKeyname = templateKeyname;
            return this;
        }

        public StPlanTemplateItem.PK rn(int rn) {
            this.rn = rn;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(templateKeyname, pk.templateKeyname) &&
                Objects.equals(rn, pk.rn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(templateKeyname, rn);
        }
    }

    @Id
    @Column(name = "template_keyname")
    @NotNull
    @Max(32)
    private String templateKeyname;

    @Id
    @Column(name = "rn")
    @NotNull
    private Integer rn;

    @Column(name = "key1")
    private Double key1;

    @Column(name = "key2")
    private Double key2;

    @Column(name = "val1")
    private Double val1;

    @Column(name = "val2")
    private Double val2;


    public String getTemplateKeyname() {
        return templateKeyname;
    }

    public void setTemplateKeyname(String templateKeyname) {
        this.templateKeyname = templateKeyname;
    }

    public Integer getRn() {
        return rn;
    }

    public void setRn(Integer rn) {
        this.rn = rn;
    }

    public Double getKey1() {
        return key1;
    }

    public void setKey1(Double key1) {
        this.key1 = key1;
    }

    public Double getKey2() {
        return key2;
    }

    public void setKey2(Double key2) {
        this.key2 = key2;
    }

    public Double getVal1() {
        return val1;
    }

    public void setVal1(Double val1) {
        this.val1 = val1;
    }

    public Double getVal2() {
        return val2;
    }

    public void setVal2(Double val2) {
        this.val2 = val2;
    }
}
