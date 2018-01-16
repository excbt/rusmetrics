package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.Objects;

public class ConsumptionFunctionLibDataHWater {

    public static ConsumptionFunction<ContServiceDataHWater> M1 = new ConsumptionFunction<>(
        "M1",
        d -> Objects.nonNull(d.getM_in()),
        d -> d.getM_in(),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> M1_sub_M2 = new ConsumptionFunction<>(
        "M1-M2",
        d -> Objects.nonNull(d.getM_in()) && Objects.nonNull(d.getM_out()),
        d -> Math.abs(d.getM_in() - d.getM_out()),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> V1 = new ConsumptionFunction<>(
        "V1",
        d -> Objects.nonNull(d.getV_in()),
        d -> d.getV_in(),
        MeasureUnitKey.V_M3.name());

    public static ConsumptionFunction<ContServiceDataHWater> V1_sub_V2 = new ConsumptionFunction<>(
        "V1-V2",
        d -> Objects.nonNull(d.getV_in()) && Objects.nonNull(d.getV_out()),
        d -> Math.abs(d.getV_in() - d.getV_out()), MeasureUnitKey.V_M3.name());

    public static ConsumptionFunction<ContServiceDataHWater> H1 = new ConsumptionFunction<>(
        "H1",
        d -> Objects.nonNull(d.getH_in()),
        d -> d.getH_in(),
        MeasureUnitKey.W_GCAL.name());

    public static ConsumptionFunction<ContServiceDataHWater> H1_sub_H2 = new ConsumptionFunction<>(
        "H1-H2",
        d -> Objects.nonNull(d.getH_in()) && Objects.nonNull(d.getH_out()),
        d -> Math.abs(d.getH_in() - d.getH_out()),
        MeasureUnitKey.W_GCAL.name());

}
