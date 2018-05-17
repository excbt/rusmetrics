package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.domain.QAbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.QContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ContObjectAccessService {

    private final static QContObjectAccess qContObjectAccess = QContObjectAccess.contObjectAccess;

    private final ContObjectAccessRepository contObjectAccessRepository;
    private final ContObjectAccessMapper contObjectAccessMapper;

    public ContObjectAccessService(ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessMapper contObjectAccessMapper) {
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessMapper = contObjectAccessMapper;
    }

    private static BooleanExpression searchCondition(String s) {
        if (s.isEmpty()) {
            return null;
        }
        return qContObjectAccess.contObject().name.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s))
            .or(qContObjectAccess.contObject().fullName.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)))
            .or(qContObjectAccess.contObject().fullAddress.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)));
    }


    @Transactional(readOnly = true)
    public Page<ContObjectAccessDTO> getContObjectAccess(PortalUserIds portalUserIds,
                                                         Optional<String> searchStringOptional,
                                                         Pageable pageable) {


        BooleanExpression subscriberFilter = qContObjectAccess.subscriberId.eq(portalUserIds.getSubscriberId());


        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter);

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<ContObjectAccess> resultPage = contObjectAccessRepository.findAll(where,pageable);

        return resultPage.map(contObjectAccessMapper::toDto);
    }


}
