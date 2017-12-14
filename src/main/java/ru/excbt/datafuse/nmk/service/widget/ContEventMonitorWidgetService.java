package ru.excbt.datafuse.nmk.service.widget;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventCategoryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventTypeDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventCategoryRepository;
import ru.excbt.datafuse.nmk.data.repository.widget.ContEventMonitorWidgetRepository;
import ru.excbt.datafuse.nmk.data.service.ContEventLevelColorV2Service;
import ru.excbt.datafuse.nmk.service.mapper.ContEventCategoryMapper;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

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

    @Autowired
    public ContEventMonitorWidgetService(ContEventMonitorWidgetRepository monitorWidgetRepository, ContEventTypeRepository contEventTypeRepository, ContEventLevelColorV2Service contEventLevelColorV2Service, ContEventCategoryRepository contEventCategoryRepository, ContEventCategoryMapper contEventCategoryMapper) {
        this.monitorWidgetRepository = monitorWidgetRepository;
        this.contEventTypeRepository = contEventTypeRepository;
        this.contEventLevelColorV2Service = contEventLevelColorV2Service;
        this.contEventCategoryRepository = contEventCategoryRepository;
        this.contEventCategoryMapper = contEventCategoryMapper;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContObjectStats {

        private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        private final Long contObjectId;
        private final Long contEventTypeId;
        private final LocalDateTime lastContEventTime;
        private final Integer count;
        private ContEventTypeDTO contEventType;

        private ContObjectStats(Object[] qryRow) {
            this.contObjectId = DBRowUtil.asLong(qryRow[0]);
            this.contEventTypeId = DBRowUtil.asLong(qryRow[1]);
            this.lastContEventTime = (LocalDateTime) qryRow[2];
            this.count = DBRowUtil.asInteger(qryRow[3]);
        }

        public Long getContObjectId() {
            return contObjectId;
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
            return "ContObjectStats{" +
                "contObjectId=" + contObjectId +
                ", contEventTypeId=" + contEventTypeId +
                ", lastContEventTime=" + lastContEventTime +
                ", count=" + count +
                '}';
        }
    }


    /**
     *
     * @return
     */
    public List<ContObjectStats> loadMonitorData(Predicate<Long> contObjectFilter, boolean nestedTypes) {
        List<ContObjectStats> statsObjList = monitorWidgetRepository.findContObjectStats()
            .stream().map(ContObjectStats::new).filter(i -> contObjectFilter.test(i.contObjectId)).collect(Collectors.toList());

        if (nestedTypes) {
            List<Long> contEventTypeIds = statsObjList.stream().map(ContObjectStats::getContEventTypeId).distinct().collect(Collectors.toList());
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
    public List<ContObjectStats> loadMonitorData(boolean nestedTypes) {
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


    public void findContObjectMonitor () {

    }

}
