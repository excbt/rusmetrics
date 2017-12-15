package ru.excbt.datafuse.nmk.service.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventCategoryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventTypeDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventCategoryRepository;
import ru.excbt.datafuse.nmk.data.repository.widget.ContEventMonitorWidgetRepository;
import ru.excbt.datafuse.nmk.data.service.ContEventLevelColorV2Service;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.service.dto.ContObjectMonitorStateDTO;
import ru.excbt.datafuse.nmk.service.dto.ContZPointMonitorStateDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContEventCategoryMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ContEventMonitorWidgetService {

    private final ContEventMonitorWidgetRepository monitorWidgetRepository;

    private final ContEventTypeRepository contEventTypeRepository;

    private final ContEventLevelColorV2Service contEventLevelColorV2Service;

    private final ContEventCategoryRepository contEventCategoryRepository;

    private final ContEventCategoryMapper contEventCategoryMapper;

    private final ContObjectRepository contObjectRepository;

    private final ContZPointRepository contZPointRepository;

    private final ObjectAccessService objectAccessService;

    private final ContObjectMapper contObjectMapper;

    private final ContZPointMapper contZPointMapper;

    @Autowired
    public ContEventMonitorWidgetService(ContEventMonitorWidgetRepository monitorWidgetRepository, ContEventTypeRepository contEventTypeRepository, ContEventLevelColorV2Service contEventLevelColorV2Service, ContEventCategoryRepository contEventCategoryRepository, ContEventCategoryMapper contEventCategoryMapper, ContObjectRepository contObjectRepository, ContZPointRepository contZPointRepository, ObjectAccessService objectAccessService, ContObjectMapper contObjectMapper, ContZPointMapper contZPointMapper) {
        this.monitorWidgetRepository = monitorWidgetRepository;
        this.contEventTypeRepository = contEventTypeRepository;
        this.contEventLevelColorV2Service = contEventLevelColorV2Service;
        this.contEventCategoryRepository = contEventCategoryRepository;
        this.contEventCategoryMapper = contEventCategoryMapper;
        this.contObjectRepository = contObjectRepository;
        this.contZPointRepository = contZPointRepository;
        this.objectAccessService = objectAccessService;
        this.contObjectMapper = contObjectMapper;
        this.contZPointMapper = contZPointMapper;
    }



    public static class MonitorEventInfo {

        private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        private final Long contEventTypeId;
        private final LocalDateTime lastContEventTime;
        private final Integer count;
        private ContEventTypeDTO contEventType;

        private MonitorEventInfo(Object[] qryRow) {
            Objects.requireNonNull(qryRow);

            this.contEventTypeId = DBRowUtil.asLong(qryRow[1]);
            this.lastContEventTime = (LocalDateTime) qryRow[2];
            this.count = DBRowUtil.asInteger(qryRow[3]);
        }


        public Long getContEventTypeId() {
            return contEventTypeId;
        }


        public String getLastContEventTime() {
            return lastContEventTime != null ? lastContEventTime.format(DateTimeFormatter.ISO_DATE_TIME) : null;
        }

        public Integer getCount() {
            return count;
        }


        public ContEventTypeDTO getContEventType() {
            return contEventType;
        }

        public void setContEventType(ContEventTypeDTO contEventType) {
            this.contEventType = contEventType;
        }

        @Override
        public String toString() {
            return "MonitorStats{" +
                "contEventTypeId=" + contEventTypeId +
                ", lastContEventTime=" + lastContEventTime +
                ", count=" + count +
                '}';
        }
    }



    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContObjectEventInfo extends MonitorEventInfo {

        private final Long contObjectId;

        private ContObjectEventInfo(Object[] qryRow) {
            super(qryRow);
            this.contObjectId = DBRowUtil.asLong(qryRow[0]);
        }

        public Long getContObjectId() {
            return contObjectId;
        }

        @Override
        public String toString() {
            return "ContObjectStats{" +
                "contObjectId=" + contObjectId +
                "} " + super.toString();
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContZPointEventInfo extends MonitorEventInfo {

        private final Long contZPointId;

        private ContZPointEventInfo(Object[] qryRow) {
            super(qryRow);
            this.contZPointId = DBRowUtil.asLong(qryRow[0]);
        }

        public Long getContZPointId() {
            return contZPointId;
        }

        @Override
        public String toString() {
            return "ContZPointStats{" +
                "contZPointId=" + contZPointId +
                "} " + super.toString();
        }
    }


    /**
     *
     * @return
     */
    public List<ContObjectEventInfo> loadMonitorData(Predicate<Long> contObjectFilter, boolean nestedTypes) {
        List<ContObjectEventInfo> statsObjList = monitorWidgetRepository.findContObjectStats()
            .stream().map(ContObjectEventInfo::new).filter(i -> contObjectFilter.test(i.contObjectId)).collect(Collectors.toList());

        if (nestedTypes) {
            List<Long> contEventTypeIds = statsObjList.stream().map(ContObjectEventInfo::getContEventTypeId).distinct().collect(Collectors.toList());
            List<ContEventTypeDTO> types = contEventTypeRepository.selectContEventTypes(contEventTypeIds).stream().map(ContEventTypeDTO.MAPPER::toDto).collect(Collectors.toList());
            types.forEach(i -> i.setLevelColor(contEventLevelColorV2Service.getColorName(i.getContEventLevel())));
            Map<Long, ContEventTypeDTO> typeMap = types.stream().collect(Collectors.toMap(ContEventTypeDTO::getId, Function.identity()));
            statsObjList.forEach(i -> i.setContEventType(typeMap.get(i.getContEventTypeId())));
        }
        return statsObjList;
    }


    /**
     *
     * @return
     */
    public List<ContObjectEventInfo> loadMonitorData(boolean nestedTypes) {
        return loadMonitorData(i -> true, nestedTypes);
    }


    /**
     *
     * @return
     */
    public List<ContEventTypeDTO> findMonitorContEventTypes() {
        List<ContEventTypeDTO> types = contEventTypeRepository.selectBaseEventTypes(true).stream().filter(ObjectFilters.NO_DISABLED_OBJECT_PREDICATE)
            .filter(i -> i.getContEventLevel() != null)
            .map(ContEventTypeDTO.MAPPER::toDto).collect(Collectors.toList());
        types.forEach(i -> i.setLevelColor(contEventLevelColorV2Service.getColorName(i.getContEventLevel())));
        return types;
    }


    private final static Comparator<ContEventCategory> sortCategory = Comparator.comparing(ContEventCategory::getCategoryOrder, Comparator.nullsLast(Comparator.naturalOrder()));

    /**
     *
     * @return
     */
    public List<ContEventCategoryDTO> findMonitorContEventCategories() {
        return contEventCategoryRepository.findAll().stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .sorted(sortCategory).map(c -> contEventCategoryMapper.toDto(c)).collect(Collectors.toList());
    }


    /**
     *
     * @param contObjectId
     * @param portalUserIds
     * @return
     */
    public ContObjectMonitorStateDTO findContObjectMonitorState (Long contObjectId, PortalUserIds portalUserIds) {

        ContObject contObject = contObjectRepository.findOne(contObjectId);
        if (contObject == null) {
            DBExceptionUtil.entityNotFoundException(ContObject.class, contObjectId);
        }

        Predicate<ContZPoint> checkAccess = objectAccessService.objectAccessUtil().checkContZPoint(portalUserIds);
        Predicate<Long> contZPointIdAccess = objectAccessService.objectAccessUtil().checkContZPointId(portalUserIds);

        List<ContZPoint> contZPoints = contZPointRepository.findByContObjectId(contObjectId)
            .stream().filter(i -> contZPointIdAccess.test(i.getId())).collect(Collectors.toList());

        List<ContZPointEventInfo> statsObjList = monitorWidgetRepository.findContZPointStats(contObjectId)
            .stream().map(ContZPointEventInfo::new).filter(i -> contZPointIdAccess.test(i.getContZPointId())).collect(Collectors.toList());


        List<Long> contEventTypeIds = statsObjList.stream().map(ContZPointEventInfo::getContEventTypeId).distinct().collect(Collectors.toList());

        Map<Long, ContEventType> contEventTypeMap = contEventTypeIds.isEmpty() ? Collections.emptyMap() :
            contEventTypeRepository.selectContEventTypes(contEventTypeIds)
            .stream().filter(i -> i.getContEventLevel() != null)
            .collect(Collectors.toMap(ContEventType::getId, Function.identity()));

        Comparator<ContEventType> CMP_BY_COLOR_LEVEL = Comparator.comparingInt(ContEventType::getContEventLevel);


        ContObjectMonitorStateDTO monitorStateDTO = new ContObjectMonitorStateDTO();

        Objects.requireNonNull(monitorStateDTO.getContZPointMonitorState());

        for (ContZPoint zPoint: contZPoints) {

            Optional<ContEventType> worseContEventType = statsObjList.stream()
                .filter(i -> i.getContZPointId().equals(zPoint.getId()))
                .map(i -> contEventTypeMap.get(i.getContEventTypeId())).filter(Objects::nonNull)
                .sorted(CMP_BY_COLOR_LEVEL).findFirst();

            ContZPointMonitorStateDTO zPointMonitorState = contZPointMapper.toMonitorStateDTO(zPoint);
            zPointMonitorState.setStateColor(worseContEventType
                .map(i -> contEventLevelColorV2Service.getColorName(i.getContEventLevel()))
                .orElse(ContEventLevelColorKeyV2.GREEN.getKeyname()));

            monitorStateDTO.getContZPointMonitorState().add(zPointMonitorState);
        }

        monitorStateDTO.setContObjectShortInfo(contObjectMapper.toShortInfoVM(contObject));

        return monitorStateDTO;
    }

}
