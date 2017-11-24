package ru.excbt.datafuse.nmk.data.model.dto;

public class SubscrStPlanDTO {

    private Long id;

    private Long subscriberId;

    private String settingMode;

    private String stPlanTemplateKey;

    private String spType;

    private String contServiceType;

    private Long localPlaceId;

    private Long rsoOrganizationId;

    private String spName;

    private String spUnitKeyname;

    private String spUnitMu;

    private Double spVal;

    private String spValMu;

    private String spPeriod;

    private Boolean isMandatory;

    private Boolean isEnabled;

    private Boolean isDeviationEnable;

    private Double deviationVal;

    private String deviationMu;

    private Boolean isChartEnable;

    private Boolean chartSingleKey;

    private String chartKeyMu;

    private String chartKey1Keyname;

    private String chartKey2Keyname;

    private String chartValMu;

    private String chartVal1Keyname;

    private String chartVal2Keyname;

    private String chartKey1Caption;

    private String chartKey2Caption;

    private String chartVal1Caption;

    private String chartVal2Caption;

    private Boolean chartKey1I;

    private Boolean chartVal1I;

    private Boolean chartKey2I;

    private Boolean chartVal2I;

    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getSettingMode() {
        return settingMode;
    }

    public void setSettingMode(String settingMode) {
        this.settingMode = settingMode;
    }

    public String getStPlanTemplateKey() {
        return stPlanTemplateKey;
    }

    public void setStPlanTemplateKey(String stPlanTemplateKey) {
        this.stPlanTemplateKey = stPlanTemplateKey;
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

    public Long getLocalPlaceId() {
        return localPlaceId;
    }

    public void setLocalPlaceId(Long localPlaceId) {
        this.localPlaceId = localPlaceId;
    }

    public Long getRsoOrganizationId() {
        return rsoOrganizationId;
    }

    public void setRsoOrganizationId(Long rsoOrganizationId) {
        this.rsoOrganizationId = rsoOrganizationId;
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
        return chartSingleKey;
    }

    public void setChartSingleKey(Boolean chartSingleKey) {
        this.chartSingleKey = chartSingleKey;
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
    public String toString() {
        return "SubscrStPlanDTO{" +
            "id=" + id +
            ", subscriberId=" + subscriberId +
            ", settingMode='" + settingMode + '\'' +
            ", stPlanTemplateKey='" + stPlanTemplateKey + '\'' +
            ", spType='" + spType + '\'' +
            ", contServiceType='" + contServiceType + '\'' +
            ", localPlaceId=" + localPlaceId +
            ", rsoOrganizationId=" + rsoOrganizationId +
            ", spName='" + spName + '\'' +
            ", spUnitKeyname='" + spUnitKeyname + '\'' +
            ", spUnitMu='" + spUnitMu + '\'' +
            ", spVal=" + spVal +
            ", spValMu='" + spValMu + '\'' +
            ", spPeriod='" + spPeriod + '\'' +
            ", isMandatory=" + isMandatory +
            ", isEnabled=" + isEnabled +
            ", isDeviationEnable=" + isDeviationEnable +
            ", deviationVal=" + deviationVal +
            ", deviationMu='" + deviationMu + '\'' +
            ", isChartEnable=" + isChartEnable +
            ", chartSingleKey=" + chartSingleKey +
            ", chartKeyMu='" + chartKeyMu + '\'' +
            ", chartKey1Keyname='" + chartKey1Keyname + '\'' +
            ", chartKey2Keyname='" + chartKey2Keyname + '\'' +
            ", chartValMu='" + chartValMu + '\'' +
            ", chartVal1Keyname='" + chartVal1Keyname + '\'' +
            ", chartVal2Keyname='" + chartVal2Keyname + '\'' +
            ", chartKey1Caption='" + chartKey1Caption + '\'' +
            ", chartKey2Caption='" + chartKey2Caption + '\'' +
            ", chartVal1Caption='" + chartVal1Caption + '\'' +
            ", chartVal2Caption='" + chartVal2Caption + '\'' +
            ", chartKey1I=" + chartKey1I +
            ", chartVal1I=" + chartVal1I +
            ", chartKey2I=" + chartKey2I +
            ", chartVal2I=" + chartVal2I +
            ", version=" + version +
            '}';
    }
}
