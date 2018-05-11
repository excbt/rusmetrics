package ru.excbt.datafuse.nmk.service.dto;

import java.util.Date;

public class SubscrPriceListDTO {

    private Long id;

    private Long srcPriceListId;

    private String priceListName;

    private Long rmaSubscriberId;

    private Long subscriberId;

    private Integer priceListLevel;

    private Integer priceListType;

    private String priceListKeyname;

    private String priceListCurrency;

    private String priceOption;

    private Date planBeginDate;

    private Date planEndDate;

    private Date factBeginDate;

    private Date factEndDate;

    private Boolean isActive = false;

    private Boolean isDisabled;

    private Boolean isMaster;

    private Boolean isDraft = true;

    private Boolean isArchive = false;

    private Long masterPriceListId;

    private int version;
}
