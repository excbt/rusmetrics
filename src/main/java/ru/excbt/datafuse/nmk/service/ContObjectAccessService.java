package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.data.model.QContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.QContZPointAccess;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;
import ru.excbt.datafuse.nmk.service.dto.ContZPointAccessDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointAccessMapper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContObjectAccessService {

    private final static QContObjectAccess qContObjectAccess = QContObjectAccess.contObjectAccess;
    private final static QContZPointAccess qContZPointAccess = QContZPointAccess.contZPointAccess;

    private final ContObjectAccessRepository contObjectAccessRepository;
    private final ContObjectAccessMapper contObjectAccessMapper;
    private final ContZPointAccessRepository contZPointAccessRepository;
    private final ContZPointAccessMapper contZPointAccessMapper;

    public ContObjectAccessService(ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessMapper contObjectAccessMapper, ContZPointAccessRepository contZPointAccessRepository, ContZPointAccessMapper contZPointAccessMapper) {
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessMapper = contObjectAccessMapper;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessMapper = contZPointAccessMapper;
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
    public Page<ContObjectAccessDTO> getContObjectAccessPage(PortalUserIds portalUserIds,
                                                         Optional<String> searchStringOptional,
                                                         Pageable pageable) {


        BooleanExpression subscriberFilter = qContObjectAccess.subscriberId.eq(portalUserIds.getSubscriberId());


        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter);

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<ContObjectAccess> resultPage = contObjectAccessRepository.findAll(where,pageable);

        return resultPage.map(contObjectAccessMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ContZPointAccessDTO> getContZPointAccess(PortalUserIds portalUserIds,
                                                         Long contObjectId) {

        BooleanExpression subscriberFilter = qContZPointAccess.subscriberId.eq(portalUserIds.getSubscriberId());
        BooleanExpression contObjectFilter = qContZPointAccess.contZPoint().contObjectId.eq(contObjectId)
            .and(qContZPointAccess.contZPoint().deleted.eq(0));
        
        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter).and(contObjectFilter);

        //Collections.
        List<ContZPointAccess> resultList = new ArrayList<>();
        contZPointAccessRepository.findAll(where).forEach(resultList::add);

        return resultList.stream().map(contZPointAccessMapper::toDto).collect(Collectors.toList());
    }

}
