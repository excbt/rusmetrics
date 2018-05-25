package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.QContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.PTreeNodeMonitorService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContObjectQueryDSLUtil {

    private ContObjectQueryDSLUtil() {
    }

    /**
     *
     * @param queryFactory
     * @param inContObjectIds
     * @param contServiceTypeKey
     * @return
     */
    public static List<Long> filterContObjectIdByContServiceType(final JPAQueryFactory queryFactory,
                                                                 final List<Long> inContObjectIds,
                                                                 final ContServiceTypeKey contServiceTypeKey) {

        QContZPoint qContZPoint = QContZPoint.contZPoint;

        // Take contZPointId of requested contServiceType
        List<Long> filteredContObjectIds = queryFactory
            .select(qContZPoint.id, qContZPoint.contObjectId, qContZPoint.contServiceTypeKeyname).from(qContZPoint)
            .where(qContZPoint.contObjectId.in(inContObjectIds).and(qContZPoint.deleted.eq(0)))
            .fetch().stream()
            .filter(t -> contServiceTypeKey.getKeyname().equals(t.get(qContZPoint.contServiceTypeKeyname)))
            .map(t -> t.get(qContZPoint.contObjectId))
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        return filteredContObjectIds;
    }


    public static List<Long> filterContZPointIdByContServiceType(final JPAQueryFactory queryFactory,
                                                                 final List<Long> inContZPointId,
                                                                 final List<String> contServiceTypeKeynameList) {

        Objects.requireNonNull(contServiceTypeKeynameList);

        if (inContZPointId.isEmpty()) return Collections.emptyList();

        QContZPoint qContZPoint = QContZPoint.contZPoint;

        // Take contZPointId of requested contServiceType
        List<Long> filteredContObjectIds = queryFactory
            .select(qContZPoint.id, qContZPoint.contServiceTypeKeyname).from(qContZPoint).where(qContZPoint.id.in(inContZPointId))
            .fetch().stream()
            .filter(t -> contServiceTypeKeynameList.contains(t.get(qContZPoint.contServiceTypeKeyname)))
            .map(t -> t.get(qContZPoint.id))
            .collect(Collectors.toList());

        return filteredContObjectIds;
    }


    /**
     *
     * @param queryFactory
     * @param inContZPointIds
     * @return
     */
    public static Map<Long, String> getContZPointServiceTypes(final JPAQueryFactory queryFactory,
                                                              final List<Long> inContZPointIds) {
        QContZPoint qContZPoint = QContZPoint.contZPoint;
        return queryFactory
            .select(qContZPoint.id, qContZPoint.contServiceTypeKeyname)
            .from(qContZPoint).where(qContZPoint.id.in(inContZPointIds))
            .fetch().stream()
            .collect(Collectors.toMap(x -> x.get(qContZPoint.id), y -> y.get(qContZPoint.contServiceTypeKeyname)));
    }

}
