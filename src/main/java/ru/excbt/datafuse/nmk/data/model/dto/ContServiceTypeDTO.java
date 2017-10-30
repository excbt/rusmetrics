package ru.excbt.datafuse.nmk.data.model.dto;

public class ContServiceTypeDTO {

    private String keyname;

    private String caption;

    private String name;

    private Integer serviceOrder;

    private String captionShort;


    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(Integer serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getCaptionShort() {
        return captionShort;
    }

    public void setCaptionShort(String captionShort) {
        this.captionShort = captionShort;
    }
}
