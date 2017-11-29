package ru.excbt.datafuse.nmk.data.model.dto;

public class SubscrStPlanChartDTO {

    private Integer rn;

    private Double key1;

    private Double key2;

    private Double val1;

    private Double val2;


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

    @Override
    public String toString() {
        return "SubscrStPlanChartDTO{" +
            "rn=" + rn +
            ", key1=" + key1 +
            ", key2=" + key2 +
            ", val1=" + val1 +
            ", val2=" + val2 +
            '}';
    }
}
