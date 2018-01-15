package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.*;

public class ConsumptionFunctionLib {

    private ConsumptionFunctionLib() {
    }


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

    public static ConsumptionFunction<ContServiceDataHWater> cons_H1 = new ConsumptionFunction<>(
        "H1",
        d -> Objects.nonNull(d.getH_in()),
        d -> d.getH_in(),
        MeasureUnitKey.W_GCAL.name());

    public static ConsumptionFunction<ContServiceDataHWater> cons_H1_sub_H2 = new ConsumptionFunction<>(
        "H1-H2",
        d -> Objects.nonNull(d.getH_in()) && Objects.nonNull(d.getH_out()),
        d -> Math.abs(d.getH_in() - d.getH_out()),
        MeasureUnitKey.W_GCAL.name());


    /**
     *
     * @param contZPoint
     * @return
     */
    public static List<ConsumptionFunction<ContServiceDataHWater>> findHWaterFunc(ContZPoint contZPoint) {
        List<ConsumptionFunction<ContServiceDataHWater>> consumptionFunctions = new ArrayList<>();

        if (checkAnyService(contZPoint, ContServiceTypeKey.HEAT)) {
            consumptionFunctions.add(Boolean.TRUE.equals(contZPoint.getDoublePipe()) ?
                cons_H1_sub_H2 : cons_H1);
        }
        if (checkAnyService(contZPoint, ContServiceTypeKey.CW, ContServiceTypeKey.HW)) {
            boolean vCase = contZPoint.getConsFields().stream().anyMatch(i -> "V".equals(i.getFieldName()));
            boolean mCase = contZPoint.getConsFields().stream().anyMatch(i -> "M".equals(i.getFieldName()));
            boolean doublePipe = Boolean.TRUE.equals(contZPoint.getDoublePipe());
            if (mCase) {
                consumptionFunctions.add(doublePipe ? cons_M1_sub_M2 : cons_M1);
            }
            if (vCase) {
                consumptionFunctions.add(doublePipe ? cons_V1_sub_V2 : cons_V1);
            }

            // No settings. Calculate all
            if (consumptionFunctions.isEmpty()) {
                consumptionFunctions.add(doublePipe ? cons_M1_sub_M2 : cons_M1);
                consumptionFunctions.add(doublePipe ? cons_V1_sub_V2 : cons_V1);
            }
        }
        return consumptionFunctions;
    }

    public static List<ConsumptionFunction<ContServiceDataElCons>> findElConsFunc(ContZPoint contZPoint) {
        List<ConsumptionFunction<ContServiceDataHWater>> consumptionFunctions = new ArrayList<>();


        return Collections.emptyList();
    }

        /**
         *
         * @param contZPoint
         * @param serviceKeys
         * @return
         */
    private static boolean checkAnyService(ContZPoint contZPoint, ContServiceTypeKey ... serviceKeys) {
        Objects.requireNonNull(serviceKeys);
        Objects.requireNonNull(contZPoint);
        boolean res = false;
        for (ContServiceTypeKey key : serviceKeys) {
            if (key.getKeyname().equals(contZPoint.getContServiceTypeKeyname())) {
                res = true;
                break;
            }
        }
        return res;
    }


    public static <T> double[] allValues (List<T> data, Comparator<T> cmp, ConsumptionFunction<T> consFunc) {

        double[] consValues = data.stream()
            .filter(d -> consFunc.getFilter().test(d))
            .sorted(cmp)
            .map(d -> consFunc.getFunc().apply(d))
            .mapToDouble(x -> x).toArray();

        return consValues;
    }


}
