package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.Objects;
import java.util.function.Function;

public class ConsumptionFunctionLibDataElCons {

    private ConsumptionFunctionLibDataElCons() {
    }

    public static final ConsumptionFunction<ContServiceDataElCons> P_Ap = createInternal ("P_Ap", d -> d.getP_Ap());
    public static final ConsumptionFunction<ContServiceDataElCons> P_Ap1 = createInternal ("P_Ap1", d -> d.getP_Ap1());
    public static final ConsumptionFunction<ContServiceDataElCons> P_Ap2 = createInternal ("P_Ap2", d -> d.getP_Ap2());
    public static final ConsumptionFunction<ContServiceDataElCons> P_Ap3 = createInternal ("p_Ap3", d -> d.getP_Ap3());
    public static final ConsumptionFunction<ContServiceDataElCons> P_Ap4 = createInternal ("p_Ap4", d -> d.getP_Ap4());

    public static final ConsumptionFunction<ContServiceDataElCons> P_An = createInternal ("P_An", d -> d.getP_An());
    public static final ConsumptionFunction<ContServiceDataElCons> P_An1 = createInternal ("P_An1", d -> d.getP_An1());
    public static final ConsumptionFunction<ContServiceDataElCons> P_An2 = createInternal ("P_An2", d -> d.getP_An2());
    public static final ConsumptionFunction<ContServiceDataElCons> P_An3 = createInternal ("P_An3", d -> d.getP_An3());
    public static final ConsumptionFunction<ContServiceDataElCons> P_An4 = createInternal ("P_An4", d -> d.getP_An4());

    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rp = createInternal ("Q_Rp", d -> d.getQ_Rp());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rp1 = createInternal ("Q_Rp1", d -> d.getQ_Rp1());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rp2 = createInternal ("Q_Rp2", d -> d.getQ_Rp2());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rp3 = createInternal ("Q_Rp3", d -> d.getQ_Rp3());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rp4 = createInternal ("Q_Rp4", d -> d.getQ_Rp4());

    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rn = createInternal ("Q_Rn", d -> d.getQ_Rn());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rn1 = createInternal ("Q_Rn1", d -> d.getQ_Rn1());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rn2 = createInternal ("Q_Rn2", d -> d.getQ_Rn2());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rn3 = createInternal ("Q_Rn3", d -> d.getQ_Rn3());
    public static final ConsumptionFunction<ContServiceDataElCons> Q_Rn4 = createInternal ("Q_Rn4", d -> d.getQ_Rn4());



    /**
     *
     * @param name
     * @param valueGetter
     * @param <T>
     * @return
     */
    private static <T> ConsumptionFunction<T> createInternal (final String name, final Function<T, Double> valueGetter) {
        return new ConsumptionFunction<>(
            name,
            d -> valueGetter.apply(d),
            MeasureUnitKey.PWR_KWT_H.name());
    }

}
