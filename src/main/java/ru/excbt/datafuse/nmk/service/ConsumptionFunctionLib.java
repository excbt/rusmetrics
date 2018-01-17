package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.*;
import java.util.stream.Collectors;

public class ConsumptionFunctionLib {

    private ConsumptionFunctionLib() {
    }


    /**
     *
     * @param contZPoint
     * @return
     */
    public static List<ConsumptionFunction<ContServiceDataHWater>> findHWaterFunc(ContZPoint contZPoint) {
        List<ConsumptionFunction<ContServiceDataHWater>> consumptionFunctions = new ArrayList<>();

        if (checkAnyService(contZPoint, ContServiceTypeKey.HEAT)) {
            consumptionFunctions.add(Boolean.TRUE.equals(contZPoint.getDoublePipe()) ?
                ConsumptionFunctionLibDataHWater.H1_sub_H2 : ConsumptionFunctionLibDataHWater.H1);
        }
        if (checkAnyService(contZPoint, ContServiceTypeKey.CW, ContServiceTypeKey.HW)) {
            List<String> fields = contZPoint.getConsFields().stream().filter(i -> Boolean.TRUE.equals(i.getIsEnabled()))
                .map(i -> i.getFieldName()).collect(Collectors.toList());
            boolean vCase = fields.contains("V");
            boolean mCase = fields.contains("M");
            boolean doublePipe = Boolean.TRUE.equals(contZPoint.getDoublePipe());
            if (mCase) {
                consumptionFunctions.add(doublePipe ? ConsumptionFunctionLibDataHWater.M1_sub_M2 : ConsumptionFunctionLibDataHWater.M1);
            }
            if (vCase) {
                consumptionFunctions.add(doublePipe ? ConsumptionFunctionLibDataHWater.V1_sub_V2 : ConsumptionFunctionLibDataHWater.V1);
            }

            // No settings. Calculate all
            if (consumptionFunctions.isEmpty()) {
                consumptionFunctions.add(doublePipe ? ConsumptionFunctionLibDataHWater.M1_sub_M2 : ConsumptionFunctionLibDataHWater.M1);
                consumptionFunctions.add(doublePipe ? ConsumptionFunctionLibDataHWater.V1_sub_V2 : ConsumptionFunctionLibDataHWater.V1);
            }
        }
        return consumptionFunctions;
    }

    public static List<ConsumptionFunction<ContServiceDataElCons>> findElConsFunc(ContZPoint contZPoint) {
        List<ConsumptionFunction<ContServiceDataElCons>> consumptionFunctions = new ArrayList<>();

        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_Ap);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_Ap1);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_Ap2);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_Ap3);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_Ap4);

        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_An);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_An1);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_An2);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_An3);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.P_An4);

        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rp);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rp1);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rp2);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rp3);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rp4);

        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rn);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rn1);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rn2);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rn3);
        consumptionFunctions.add(ConsumptionFunctionLibDataElCons.Q_Rn4);

        return consumptionFunctions;
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


    /**
     *
     * @param data
     * @param cmp
     * @param consFunc
     * @param <T>
     * @return
     */
    public static <T> double[] allValues (List<T> data, Comparator<T> cmp, ConsumptionFunction<T> consFunc) {

        double[] consValues = data.stream()
            .filter(d -> consFunc.getDataFilter().test(d))
            .sorted(cmp)
            .map(d -> consFunc.getValueFunction().apply(d))
            .map(d -> consFunc.postProcessingRound(d))
            .mapToDouble(x -> x).toArray();

        return consValues;
    }

    /**
     *
     * @param data
     * @param cmp
     * @param consFunc
     * @param <T>
     * @return
     */
    public static <T> Optional<Double> lastValue (List<T> data, Comparator<T> cmp, ConsumptionFunction<T> consFunc) {
        if (data == null) {
            return Optional.empty();
        }

        Optional<Double> consValue = data.stream()
            .filter(d -> consFunc.getDataFilter().test(d))
            .sorted(cmp.reversed())
            .filter(d -> consFunc.getNotZeroFilter().test(d))
            .map(d -> consFunc.getValueFunction().apply(d))
            .findFirst()
            .map(i -> consFunc.postProcessingRound(i));

        return consValue;
    }



}
