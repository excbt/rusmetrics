package ru.excbt.datafuse.nmk.service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;

import java.util.Objects;

public class ConsumptionFunctionLibDataImpulse {

    private ConsumptionFunctionLibDataImpulse() {
    }

    public static ConsumptionFunction<ContServiceDataImpulse> EL_PWR = createInternal ("dataValue", MeasureUnitKey.PWR_KWT_H);
    public static ConsumptionFunction<ContServiceDataImpulse> HWATER = createInternal ("dataValue", MeasureUnitKey.V_M3);

    /**
     *
     * @param name
     * @param measureUnitKey
     * @return
     */
    private static ConsumptionFunction<ContServiceDataImpulse> createInternal (final String name, MeasureUnitKey measureUnitKey) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(measureUnitKey);

        return new ConsumptionFunction<>(
            name,
            d -> d.getDataValue(),
            measureUnitKey.name());
    }

}
