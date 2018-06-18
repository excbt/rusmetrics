package ru.excbt.datafuse.nmk.repository.support;

import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;

public interface ContObjectMeterPeriod {
    Long getId();
    String getKey();
    MeterPeriodSetting getMeterPeriodSetting();
}
