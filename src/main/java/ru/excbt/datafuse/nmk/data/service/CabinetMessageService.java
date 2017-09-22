package ru.excbt.datafuse.nmk.data.service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDGenerator;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.support.*;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;

import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing CabinetMessage.
 */
@Service
public class CabinetMessageService {

    private final Logger log = LoggerFactory.getLogger(CabinetMessageService.class);

    private final CabinetMessageRepository cabinetMessageRepository;

    private final CabinetMessageMapper cabinetMessageMapper;

    private final SessionService sessionService;

    private final SubscriberRepository subscriberRepository;

    private final FDWSequence fdwSequence;

    public CabinetMessageService(CabinetMessageRepository cabinetMessageRepository, CabinetMessageMapper cabinetMessageMapper, SessionService sessionService, SubscriberRepository subscriberRepository) {
        this.cabinetMessageRepository = cabinetMessageRepository;
        this.cabinetMessageMapper = cabinetMessageMapper;
        this.sessionService = sessionService;
        this.subscriberRepository = subscriberRepository;
        this.fdwSequence = new FDWSequence(sessionService, "SELECT a FROM  " + DBMetadata.SCHEME_CABINET2 + ".hibernate_seq_table", 50);
    }

    public static final String INS_SQL_QRY = "INSERT INTO "+ DBMetadata.SCHEME_CABINET2 + ".cabinet_message( " +
        "id, " + //1
        "message_type, " + //2
        "message_direction, " + //3
        "from_portal_subscriber_id, " + //4
        "from_portal_user_id, " + //5
        "to_portal_subscriber_id, " + //6
        "to_portal_user_id, " + //7
        "message_subject, " + //8
        "message_body, " + //9
        "master_id, " + //10
        "response_to_id, " + //11
        "creation_date_time, " + //12
        "review_date_time," + //13
        "master_uuid) " + //14
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";


    public static final String UPD_SQL_QRY = "UPDATE " + DBMetadata.SCHEME_CABINET2 +".cabinet_message" +
        "   SET review_date_time=?" +
        " WHERE id=?;";


    private Long insertCabinetMessageSQL(CabinetMessage cabinetMessage) {
        Session session = sessionService.getSession();
        final Long id = fdwSequence.next();
        log.debug("new cabinet message id: {}", id);
        session.doWork((Connection c) -> {
            try (PreparedStatement preparedStatement = c.prepareStatement(INS_SQL_QRY)) {
                preparedStatement.setObject(1, id);
                preparedStatement.setObject(2, cabinetMessage.getMessageType());
                preparedStatement.setObject(3, cabinetMessage.getMessageDirection());
                preparedStatement.setObject(4, cabinetMessage.getFromPortalSubscriberId());
                preparedStatement.setObject(5, cabinetMessage.getFromPortalUserId());
                preparedStatement.setObject(6, cabinetMessage.getToPortalSubscriberId());
                preparedStatement.setObject(7, cabinetMessage.getToPortalUserId());
                preparedStatement.setObject(8, cabinetMessage.getMessageSubject());
                preparedStatement.setObject(9, cabinetMessage.getMessageBody());
                preparedStatement.setObject(10, cabinetMessage.getMasterId());
                preparedStatement.setObject(11, cabinetMessage.getResponseToId());

                if (cabinetMessage.getCreationDateTime() != null) {
                    preparedStatement.setTimestamp(12,
                        Timestamp.valueOf(cabinetMessage.getCreationDateTime().toLocalDateTime()));
                } else preparedStatement.setNull(12, Types.TIMESTAMP);

                if (cabinetMessage.getReviewDateTime() != null) {
                    preparedStatement.setTimestamp(13,
                        Timestamp.valueOf(cabinetMessage.getReviewDateTime().toLocalDateTime()));
                } else preparedStatement.setNull(13, Types.TIMESTAMP);

                preparedStatement.setObject(14, cabinetMessage.getMasterUuid());

                log.debug("Create CabinetMessage SQL: {}", preparedStatement.toString());
                preparedStatement.execute();
            }
        });

        return id;
    }

    private int updateCabinetMessageReviewDate(Long id, ZonedDateTime reviewDateTime) {
        Session session = sessionService.getSession();
        return session.doReturningWork((Connection c) -> {
            try (PreparedStatement preparedStatement = c.prepareStatement(UPD_SQL_QRY);) {
                if (reviewDateTime != null) {
                    preparedStatement.setTimestamp(1,
                        Timestamp.valueOf(reviewDateTime.toLocalDateTime()));
                } else preparedStatement.setNull(1, Types.TIMESTAMP);

                preparedStatement.setObject(2, id);
                log.debug("Update CabinetMessage: {}", preparedStatement.toString());
                int cnt = preparedStatement.executeUpdate();
                return cnt;
            }
        });
    }

    /**
     * Save a cabinetMessage.
     *
     * @param cabinetMessageDTO the entity to save
     * @return the persisted entity
     */
    @Transactional
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
    @Transactional
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

        return cabinetMessageRepository.findMessageChainByMasterId(messageId).stream()
            .map(cabinetMessageMapper::toDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequestToSubscriber(PortalUserIds userIds, CabinetMessageType cabinetMessageType, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByToSubscriberIds(RepositoryUtils.safeList(userIds.getSubscriberId()),
            cabinetMessageType != null ? cabinetMessageType.name() : CabinetMessageType.REQUEST.name(),
            pageable).map(cabinetMessageMapper::toDto);
    }


    @Transactional
    public List<Long> updateMessageChainReview(Long masterMessageId, PortalUserIds userIds, boolean resetReviews) {
        return cabinetMessageRepository.findMessageChainByMasterId(masterMessageId).stream()
            .filter(i -> (i.getReviewDateTime() == null && !resetReviews) ||
                (resetReviews && i.getReviewDateTime() != null))
            .filter(i -> userIds.getSubscriberId().equals(i.getToPortalSubscriberId()))
            .map(i -> {
                int cnt = updateCabinetMessageReviewDate(i.getId(), ZonedDateTime.now());
                return cnt > 0 ? i.getId() : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    @Transactional
    public UUID sendNotificationToCabinets(PortalUserIds parentIds, String messageSubject, String messageBody, List<Long> subscrCabinetIds) {

        final UUID masterUuid = Generators.timeBasedGenerator().generate();

        subscriberRepository.selectChildSubscribers(parentIds.getSubscriberId()).stream()
            .filter(i -> subscrCabinetIds == null || subscrCabinetIds.isEmpty() || subscrCabinetIds.contains(i.getId()))
            .forEach(s -> {
                CabinetMessage cabinetMessage = new CabinetMessage();
                cabinetMessage.setFromPortalSubscriberId(parentIds.getSubscriberId());
                cabinetMessage.setFromPortalUserId(parentIds.getUserId());
                cabinetMessage.setMessageDirection(CabinetMessageDirection.OUT.name());
                cabinetMessage.setMessageType(CabinetMessageType.NOTIFICATION.name());
                cabinetMessage.setToPortalSubscriberId(s.getId());
                cabinetMessage.setMessageSubject(messageSubject);
                cabinetMessage.setMessageBody(messageBody);
                cabinetMessage.setMasterUuid(masterUuid);
                cabinetMessage.setCreationDateTime(ZonedDateTime.now());
                Long messageId = insertCabinetMessageSQL(cabinetMessage);
                log.info("Cabinet Message Id: {}", messageId);

            });

        return masterUuid;
    }


}
