package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.Objects;

public class ConsumptionFunctionLib {

    public static ConsumptionFunction<ContServiceDataHWater> cons_M1 = new ConsumptionFunction<>(
        "M1",
        d -> Objects.nonNull(d.getM_in()),
        d -> d.getM_in(),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> cons_M1_sub_M2 = new ConsumptionFunction<>(
        "M1-M2",
        d -> Objects.nonNull(d.getM_in()) && Objects.nonNull(d.getM_out()),
        d -> Math.abs(d.getM_in() - d.getM_out()),
        MeasureUnitKey.M_TON.name());

    public static ConsumptionFunction<ContServiceDataHWater> cons_V1 = new ConsumptionFunction<>(
        "V1",
        d -> Objects.nonNull(d.getV_in()),
        d -> d.getV_in(),
        MeasureUnitKey.V_M3.name());

    public static ConsumptionFunction<ContServiceDataHWater> cons_V1_sub_V2 = new ConsumptionFunction<>(
        "V1-V2",
        d -> Objects.nonNull(d.getV_in()) && Objects.nonNull(d.getV_out()),
        d -> Math.abs(d.getV_in() - d.getV_out()), MeasureUnitKey.V_M3.name());

    private ConsumptionFunctionLib() {
    }

    public static ConsumptionFunction<ContServiceDataHWater> findHWaterFunc(ContZPoint contZPoint) {
        return Boolean.TRUE.equals(contZPoint.getDoublePipe()) ?
            cons_M1_sub_M2 : cons_M1;
    }

}
