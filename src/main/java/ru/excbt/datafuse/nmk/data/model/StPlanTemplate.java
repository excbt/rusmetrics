package ru.excbt.datafuse.nmk.data.model;


import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.util.List;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "st_plan_template")
public class StPlanTemplate extends AbstractKeynameEntity implements DeletedMarker {


    @Column(name = "keyname")
    @Max(32)
    private String keyname;

    @Column(name = "sp_type")
    @Max(32)
    private String spType;

    @Column(name = "cont_service_type")
    @Max(32)
    private String contServiceType;

    @Column(name = "sp_name")
    private String spName;

    @Column(name = "sp_unit_keyname")
    @Max(32)
    private String spUnitKeyname;

    @Column(name = "sp_unit_mu")
    @Max(32)
    private String spUnitMu;

    @Column(name = "sp_val")
    private Double spVal;

    @Column(name = "sp_val_mu")
    @Max(32)
    private String spValMu;

    @Column(name = "sp_period")
    private String spPeriod;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "is_deviation_enable")
    private Boolean isDeviationEnable;

    @Column(name = "deviation_val")
    private Double deviationVal;

    @Column(name = "deviation_mu")
    private String deviationMu;

    @Column(name = "is_chart_enable")
    private Boolean isChartEnable;

    @Column(name = "is_chart_single_key")
    private Boolean isChartSingleKey;

    @Column(name = "chart_key_mu")
    private String chartKeyMu;

    @Column(name = "chart_key1_keyname")
    private String chartKey1Keyname;

    @Column(name = "chart_key2_keyname")
    private String chartKey2Keyname;

    @Column(name = "chart_val_mu")
    private String chartValMu;

    @Column(name = "chart_val1_keyname")
    private String chartVal1Keyname;

    @Column(name = "chart_val2_keyname")
    private String chartVal2Keyname;

    @Column(name = "chart_key1_caption")
    private String chartKey1Caption;

    @Column(name = "chart_key2_caption")
    private String chartKey2Caption;

    @Column(name = "chart_val1_caption")
    private String chartVal1Caption;

    @Column(name = "chart_val2_caption")
    private String chartVal2Caption;

    @Column(name = "chart_key1_i")
    private Boolean chartKey1I;

    @Column(name = "chart_val1_i")
    private Boolean chartVal1I;

    @Column(name = "chart_key2_i")
    private Boolean chartKey2I;

    @Column(name = "chart_val2_i")
    private Boolean chartVal2I;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @OneToMany(mappedBy = "templateKeyname", fetch = FetchType.LAZY)
    private List<StPlanChartTemplate> chartTemplates;

    @Override
    public String getKeyname() {
        return keyname;
    }

    @Override
    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getSpType() {
        return spType;
    }

    public void setSpType(String spType) {
        this.spType = spType;
    }

    public String getContServiceType() {
        return contServiceType;
    }

    public void setContServiceType(String contServiceType) {
        this.contServiceType = contServiceType;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public String getSpUnitKeyname() {
        return spUnitKeyname;
    }

    public void setSpUnitKeyname(String spUnitKeyname) {
        this.spUnitKeyname = spUnitKeyname;
    }

    public String getSpUnitMu() {
        return spUnitMu;
    }

    public void setSpUnitMu(String spUnitMu) {
        this.spUnitMu = spUnitMu;
    }

    public Double getSpVal() {
        return spVal;
    }

    public void setSpVal(Double spVal) {
        this.spVal = spVal;
    }

    public String getSpValMu() {
        return spValMu;
    }

    public void setSpValMu(String spValMu) {
        this.spValMu = spValMu;
    }

    public String getSpPeriod() {
        return spPeriod;
    }

    public void setSpPeriod(String spPeriod) {
        this.spPeriod = spPeriod;
    }

    public Boolean getMandatory() {
        return isMandatory;
    }

    public void setMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getDeviationEnable() {
        return isDeviationEnable;
    }

    public void setDeviationEnable(Boolean deviationEnable) {
        isDeviationEnable = deviationEnable;
    }

    public Double getDeviationVal() {
        return deviationVal;
    }

    public void setDeviationVal(Double deviationVal) {
        this.deviationVal = deviationVal;
    }

    public String getDeviationMu() {
        return deviationMu;
    }

    public void setDeviationMu(String deviationMu) {
        this.deviationMu = deviationMu;
    }

    public Boolean getChartEnable() {
        return isChartEnable;
    }

    public void setChartEnable(Boolean chartEnable) {
        isChartEnable = chartEnable;
    }

    public Boolean getChartSingleKey() {
        return isChartSingleKey;
    }

    public void setChartSingleKey(Boolean chartSingleKey) {
        isChartSingleKey = chartSingleKey;
    }

    public String getChartKeyMu() {
        return chartKeyMu;
    }

    public void setChartKeyMu(String chartKeyMu) {
        this.chartKeyMu = chartKeyMu;
    }

    public String getChartKey1Keyname() {
        return chartKey1Keyname;
    }

    public void setChartKey1Keyname(String chartKey1Keyname) {
        this.chartKey1Keyname = chartKey1Keyname;
    }

    public String getChartKey2Keyname() {
        return chartKey2Keyname;
    }

    public void setChartKey2Keyname(String chartKey2Keyname) {
        this.chartKey2Keyname = chartKey2Keyname;
    }

    public String getChartValMu() {
        return chartValMu;
    }

    public void setChartValMu(String chartValMu) {
        this.chartValMu = chartValMu;
    }

    public String getChartVal1Keyname() {
        return chartVal1Keyname;
    }

    public void setChartVal1Keyname(String chartVal1Keyname) {
        this.chartVal1Keyname = chartVal1Keyname;
    }

    public String getChartVal2Keyname() {
        return chartVal2Keyname;
    }

    public void setChartVal2Keyname(String chartVal2Keyname) {
        this.chartVal2Keyname = chartVal2Keyname;
    }

    public String getChartKey1Caption() {
        return chartKey1Caption;
    }

    public void setChartKey1Caption(String chartKey1Caption) {
        this.chartKey1Caption = chartKey1Caption;
    }

    public String getChartKey2Caption() {
        return chartKey2Caption;
    }

    public void setChartKey2Caption(String chartKey2Caption) {
        this.chartKey2Caption = chartKey2Caption;
    }

    public String getChartVal1Caption() {
        return chartVal1Caption;
    }

    public void setChartVal1Caption(String chartVal1Caption) {
        this.chartVal1Caption = chartVal1Caption;
    }

    public String getChartVal2Caption() {
        return chartVal2Caption;
    }

    public void setChartVal2Caption(String chartVal2Caption) {
        this.chartVal2Caption = chartVal2Caption;
    }

    public Boolean getChartKey1I() {
        return chartKey1I;
    }

    public void setChartKey1I(Boolean chartKey1I) {
        this.chartKey1I = chartKey1I;
    }

    public Boolean getChartVal1I() {
        return chartVal1I;
    }

    public void setChartVal1I(Boolean chartVal1I) {
        this.chartVal1I = chartVal1I;
    }

    public Boolean getChartKey2I() {
        return chartKey2I;
    }

    public void setChartKey2I(Boolean chartKey2I) {
        this.chartKey2I = chartKey2I;
    }

    public Boolean getChartVal2I() {
        return chartVal2I;
    }

    public void setChartVal2I(Boolean chartVal2I) {
        this.chartVal2I = chartVal2I;
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

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public List<StPlanChartTemplate> getChartTemplates() {
        return chartTemplates;
    }

    public void setChartTemplates(List<StPlanChartTemplate> chartTemplates) {
        this.chartTemplates = chartTemplates;
    }
}
