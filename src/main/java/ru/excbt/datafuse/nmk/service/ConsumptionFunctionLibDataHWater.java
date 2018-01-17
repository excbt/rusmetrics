package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import static ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction.absNullSafe;
import static ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction.valuesNotNull;

public class ConsumptionFunctionLibDataHWater {

    public static ConsumptionFunction<ContServiceDataHWater> M1 = new ConsumptionFunction<>(
        "M1",
        d -> d.getM_in(),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> M1_sub_M2 = new ConsumptionFunction<>(
        "M1-M2",
        f -> valuesNotNull(f.getM_in(), f.getM_out()),
        d -> absNullSafe(d.getM_in(), d.getM_out()),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> V1 = new ConsumptionFunction<>(
        "V1",
        d -> d.getV_in(),
        MeasureUnitKey.V_M3.name());

    public static ConsumptionFunction<ContServiceDataHWater> V1_sub_V2 = new ConsumptionFunction<>(
        "V1-V2",
        f -> valuesNotNull(f.getV_in(), f.getV_out()),
        d -> absNullSafe (d.getV_in(),d.getV_out()),
        MeasureUnitKey.V_M3.name());

    public static ConsumptionFunction<ContServiceDataHWater> H1 = new ConsumptionFunction<>(
        "H1",
        d -> d.getH_in(),
        MeasureUnitKey.W_GCAL.name());

    public static ConsumptionFunction<ContServiceDataHWater> H1_sub_H2 = new ConsumptionFunction<>(
        "H1-H2",
        f -> valuesNotNull(f.getH_in(), f.getH_out()),
        d -> absNullSafe(d.getH_in(), d.getH_out()),
        MeasureUnitKey.W_GCAL.name());

}
