package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.QContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.PTreeNodeMonitorService;

import java.util.List;
import java.util.stream.Collectors;

public class ContObjectFilterUtil {

    private ContObjectFilterUtil() {
    }

    public static List<Long> filterContObjectIdByContServiceType(final JPAQueryFactory queryFactory,
                                                                 final List<Long> inContObjectIds,
                                                                 final ContServiceTypeKey contServiceTypeKey) {

        QContZPoint qContZPoint = QContZPoint.contZPoint;

        // Take contZPointId of requested contServiceType
        List<Long> filteredContObjectIds = queryFactory
            .select(qContZPoint.id, qContZPoint.contServiceTypeKeyname).from(qContZPoint).where(qContZPoint.id.in(inContObjectIds))
            .fetch().stream()
            .filter(t -> contServiceTypeKey.getKeyname().equals(t.get(qContZPoint.contServiceTypeKeyname)))
            .map(t -> t.get(qContZPoint.contObjectId))
            .collect(Collectors.toList());

        return filteredContObjectIds;
    }


}
