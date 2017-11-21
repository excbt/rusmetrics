package ru.excbt.datafuse.nmk.data.model;


import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "st_plan_template")
public class StPlanTemplate extends AbstractKeynameEntity implements DeletedMarker {


    @Column(name = "keyname")
    @Max(32)
    private String keyname;

    @Column(name = "sp_type")
    @Max(32)
    private String spType;

    @Column(name = "sp_name")
    private String spName;

    @Column(name = "is_single_key")
    @NotNull
    private Boolean isSingleKey = false;

    @Column(name = "key1_name")
    private String key1Name;

    @Column(name = "key1_caption")
    private String key1Caption;

    @Column(name = "key2_name")
    private String key2Name;

    @Column(name = "key2_caption")
    private String key2Caption;

    @Column(name = "val1_name")
    private String val1Name;

    @Column(name = "val1_caption")
    private String val1Caption;

    @Column(name = "val2_name")
    private String val2Name;

    @Column(name = "val2_caption")
    private String val2Caption;

    @Column(name = "default_val1")
    private Double defaultVal1 ;

    @Column(name = "default_val2")
    private Double defaultVal2;

    @Column(name = "deviation_val")
    private Double deviationVal;

    @Column(name = "deviation_mu")
    @Max(32)
    private String deviationMu;

    @Column(name = "default_key1i")
    @NotNull
    private Boolean defaultKey1i = false;

    @Column(name = "default_key2i")
    private Boolean defaultKey2i;

    @Column(name = "default_val1i")
    @NotNull
    private Boolean defaultVal1i = true;

    @Column(name = "default_val2i")
    @NotNull
    private Boolean defaultVal2i = false;

    @Column(name = "is_mandatory")
    @NotNull
    private Boolean isMandatory = false;

    @Column(name = "is_enabled")
    @NotNull
    private Boolean isEnabled = true;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;


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

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public Boolean getSingleKey() {
        return isSingleKey;
    }

    public void setSingleKey(Boolean singleKey) {
        isSingleKey = singleKey;
    }

    public String getKey1Name() {
        return key1Name;
    }

    public void setKey1Name(String key1Name) {
        this.key1Name = key1Name;
    }

    public String getKey1Caption() {
        return key1Caption;
    }

    public void setKey1Caption(String key1Caption) {
        this.key1Caption = key1Caption;
    }

    public String getKey2Name() {
        return key2Name;
    }

    public void setKey2Name(String key2Name) {
        this.key2Name = key2Name;
    }

    public String getKey2Caption() {
        return key2Caption;
    }

    public void setKey2Caption(String key2Caption) {
        this.key2Caption = key2Caption;
    }

    public String getVal1Name() {
        return val1Name;
    }

    public void setVal1Name(String val1Name) {
        this.val1Name = val1Name;
    }

    public String getVal1Caption() {
        return val1Caption;
    }

    public void setVal1Caption(String val1Caption) {
        this.val1Caption = val1Caption;
    }

    public String getVal2Name() {
        return val2Name;
    }

    public void setVal2Name(String val2Name) {
        this.val2Name = val2Name;
    }

    public String getVal2Caption() {
        return val2Caption;
    }

    public void setVal2Caption(String val2Caption) {
        this.val2Caption = val2Caption;
    }

    public Double getDefaultVal1() {
        return defaultVal1;
    }

    public void setDefaultVal1(Double defaultVal1) {
        this.defaultVal1 = defaultVal1;
    }

    public Double getDefaultVal2() {
        return defaultVal2;
    }

    public void setDefaultVal2(Double defaultVal2) {
        this.defaultVal2 = defaultVal2;
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

    public Boolean getDefaultKey1i() {
        return defaultKey1i;
    }

    public void setDefaultKey1i(Boolean defaultKey1i) {
        this.defaultKey1i = defaultKey1i;
    }

    public Boolean getDefaultKey2i() {
        return defaultKey2i;
    }

    public void setDefaultKey2i(Boolean defaultKey2i) {
        this.defaultKey2i = defaultKey2i;
    }

    public Boolean getDefaultVal1i() {
        return defaultVal1i;
    }

    public void setDefaultVal1i(Boolean defaultVal1i) {
        this.defaultVal1i = defaultVal1i;
    }

    public Boolean getDefaultVal2i() {
        return defaultVal2i;
    }

    public void setDefaultVal2i(Boolean defaultVal2i) {
        this.defaultVal2i = defaultVal2i;
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
}
