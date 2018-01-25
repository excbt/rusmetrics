package ru.excbt.datafuse.nmk.service.consumption;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

import java.util.Objects;

@Builder
@Getter
@ToString
public class ConsumptionTaskTemplate {

    public static final ConsumptionTaskTemplate Template24H_from_1H = ConsumptionTaskTemplate
        .builder()
        .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
        .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
        .build();

    public static final ConsumptionTaskTemplate Template24H_from_1H_ABS = ConsumptionTaskTemplate
        .builder()
        .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
        .srcTimeDetailType(TimeDetailKey.TYPE_1H_ABS.getKeyname())
        .build();

    public static final ConsumptionTaskTemplate Template24H_from_24H_ABS = ConsumptionTaskTemplate
        .builder()
        .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
        .srcTimeDetailType(TimeDetailKey.TYPE_1H_ABS.getKeyname())
        .build();

    private final String srcTimeDetailType;

    private final String destTimeDetailType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumptionTaskTemplate that = (ConsumptionTaskTemplate) o;
        return Objects.equals(srcTimeDetailType, that.srcTimeDetailType) &&
            Objects.equals(destTimeDetailType, that.destTimeDetailType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(srcTimeDetailType, destTimeDetailType);
    }
}
