package ru.excbt.datafuse.nmk.data.service;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;
import ru.excbt.datafuse.nmk.data.service.support.RepositoryUtils;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing CabinetMessage.
 */
@Service
//@Transactional
public class CabinetMessageService {


    private final Logger log = LoggerFactory.getLogger(CabinetMessageService.class);

    private final CabinetMessageRepository cabinetMessageRepository;

    private final CabinetMessageMapper cabinetMessageMapper;

    @PersistenceContext(unitName = "nmk-p")
    private EntityManager em;


    public CabinetMessageService(CabinetMessageRepository cabinetMessageRepository, CabinetMessageMapper cabinetMessageMapper) {
        this.cabinetMessageRepository = cabinetMessageRepository;
        this.cabinetMessageMapper = cabinetMessageMapper;
    }

    public static final String INS_SQL_QRY = "INSERT INTO cabinet2_dev.cabinet_message( " +
        "id, " +
        "message_type, " +
        "message_direction, " +
        "from_portal_subscriber_id, " +
        "from_portal_user_id, " +
        "to_portal_subscriber_id, " +
        "to_portal_user_id, " +
        "message_body, " +
        "master_id, " +
        "response_to_id, " +
        "creation_date_time, " +
        "review_date_time) " +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";


    private static void setParameterL (Query query, int position, Long value) {
        if (value != null)
            query.setParameter(position, value.longValue());
        else
            query.setParameter(position, null);
    }


    private Long insertCabinetMessageSQL(CabinetMessage cabinetMessage) {
        Session session = em.unwrap(Session.class);
        final Long id = getId();
        log.info("id: {}", id);
        session.doWork(s -> {
            PreparedStatement preparedStatement = s.prepareStatement(INS_SQL_QRY);
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, cabinetMessage.getMessageType());
            preparedStatement.setObject(3, cabinetMessage.getMessageDirection());
            preparedStatement.setObject(4, cabinetMessage.getFromPortalSubscriberId());
            preparedStatement.setObject(5, cabinetMessage.getFromPortalUserId());
            preparedStatement.setObject(6, cabinetMessage.getToPortalSubscriberId());
            preparedStatement.setObject(7, cabinetMessage.getToPortalUserId());
            preparedStatement.setObject(8, cabinetMessage.getToPortalSubscriberId());
            preparedStatement.setObject(9, cabinetMessage.getToPortalSubscriberId());
            preparedStatement.setObject(10, cabinetMessage.getToPortalSubscriberId());

            if (cabinetMessage.getCreationDateTime() != null) {
                preparedStatement.setTimestamp(11,
                    Timestamp.valueOf(cabinetMessage.getCreationDateTime().toLocalDateTime()));
            } else preparedStatement.setNull(11, Types.TIMESTAMP);

            if (cabinetMessage.getReviewDateTime() != null) {
                preparedStatement.setTimestamp(12,
                    Timestamp.valueOf(cabinetMessage.getReviewDateTime().toLocalDateTime()));
            } else preparedStatement.setNull(12, Types.TIMESTAMP);

            preparedStatement.execute();
        });

        return id;
    }

    /**
     * Save a cabinetMessage.
     *
     * @param cabinetMessageDTO the entity to save
     * @return the persisted entity
     */
    public CabinetMessageDTO save(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        log.debug("Request to save CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getId() != null) {
            //throw new IllegalStateException("Can't save existing CabinetMessageDTO");
        }

        setFromFields(cabinetMessageDTO, userIds);
        CabinetMessage cabinetMessage = cabinetMessageMapper.toEntity(cabinetMessageDTO);

        cabinetMessage.setToPortalSubscriberId(userIds.getParentSubscriberId());
        if (cabinetMessage.getMessageDirection() == null) {
            cabinetMessage.setMessageDirection(CabinetMessage.DEFAULT_DIRECTION.name());
        }
        if (cabinetMessage.getMessageType() == null) {
            cabinetMessage.setMessageType(CabinetMessage.DEFAULT_TYPE.name());
        }

        cabinetMessage.setCreationDateTime(ZonedDateTime.now());

        Long id = insertCabinetMessageSQL(cabinetMessage);

        cabinetMessage = cabinetMessageRepository.findOne(id);
        return cabinetMessageMapper.toDto(cabinetMessage);
    }


    private Long getId() {
        Query qry = em.createNativeQuery("SELECT a FROM  " + DBMetadata.SCHEME_CABINET2 + ".hibernate_seq_table");
        Long id = DBRowUtils.asLong(qry.getSingleResult());
        return id;
    }

    private static void setResponseFields(CabinetMessageDTO cabinetMessageDTO, CabinetMessage responseToMessage) {
        cabinetMessageDTO.setToPortalSubscriberId(responseToMessage.getFromPortalSubscriberId());
        cabinetMessageDTO.setToPortalUserId(responseToMessage.getFromPortalUserId());
        cabinetMessageDTO.setMessageType(CabinetMessageType.RESPONSE.name());
        cabinetMessageDTO.setMasterId(CabinetMessageType.REQUEST.name().
            equals(responseToMessage.getMessageType()) ?
            responseToMessage.getId() : responseToMessage.getMasterId());
    }

    private static void setFromFields(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        cabinetMessageDTO.setFromPortalSubscriberId(userIds.getSubscriberId());
        cabinetMessageDTO.setFromPortalUserId(userIds.getUserId());
    }


    /**
     *
     * @param cabinetMessageDTO
     * @param userIds
     * @return
     */
    public CabinetMessageDTO saveResponse(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        log.debug("Request to saveResponse CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getResponseToId() == null) {
            throw new IllegalStateException("responseToId cannot be null");
        }

        CabinetMessage responseToMessage = cabinetMessageRepository.findOne(cabinetMessageDTO.getResponseToId());
        if (responseToMessage == null) {
            throw DBExceptionUtils.entityNotFoundException(CabinetMessage.class, cabinetMessageDTO.getResponseToId());
        }

        setFromFields (cabinetMessageDTO, userIds);
        setResponseFields(cabinetMessageDTO, responseToMessage);

        return save(cabinetMessageDTO, userIds);
    }

    /**
     *  Get all the cabinetMessages.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAll(PortalUserIds userIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByFromPortalSubscriberId(userIds.getSubscriberId(),
            pageable).map(cabinetMessageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequest(PortalUserIds userIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByFromPortalSubscriberId(userIds.getSubscriberId(),
            CabinetMessageType.REQUEST.name(), pageable).map(cabinetMessageMapper::toDto);
    }


    /**
     *  Get one cabinetMessage by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public CabinetMessageDTO findOne(Long id) {
        log.debug("Request to get CabinetMessage : {}", id);
        CabinetMessage cabinetMessage = cabinetMessageRepository.findOne(id);
        return cabinetMessageMapper.toDto(cabinetMessage);
    }

    /**
     *  Delete the  cabinetMessage by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete CabinetMessage : {}", id);
        cabinetMessageRepository.delete(id);
    }


    @Transactional(readOnly = true)
    public List<CabinetMessageDTO> findMessageChain(Long messageId) {
        log.debug("Request to get all CabinetMessages");
        CabinetMessage cabinetMessage = cabinetMessageRepository.findOne(messageId);

        if (cabinetMessage == null) {
            return Collections.emptyList();
        }

        return cabinetMessageRepository.findMessageChain(messageId).stream()
            .map(cabinetMessageMapper::toDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequestToSubscriber(PortalUserIds userIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByToSubscriberIds(RepositoryUtils.safeList(userIds.getSubscriberId()),
            CabinetMessageType.REQUEST.name(),
            pageable).map(cabinetMessageMapper::toDto);
    }



}
