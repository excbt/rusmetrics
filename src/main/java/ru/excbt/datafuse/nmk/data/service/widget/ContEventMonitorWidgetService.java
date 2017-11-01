package ru.excbt.datafuse.nmk.data.service.widget;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventTypeDTO;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.widget.ContEventMonitorWidgetRepository;
import ru.excbt.datafuse.nmk.data.service.ContEventLevelColorV2Service;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ContEventMonitorWidgetService {

    private final ContEventMonitorWidgetRepository monitorWidgetRepository;

    private final ContEventTypeRepository contEventTypeRepository;

    private final ContEventLevelColorV2Service contEventLevelColorV2Service;

    @Autowired
    public ContEventMonitorWidgetService(ContEventMonitorWidgetRepository monitorWidgetRepository, ContEventTypeRepository contEventTypeRepository, ContEventLevelColorV2Service contEventLevelColorV2Service) {
        this.monitorWidgetRepository = monitorWidgetRepository;
        this.contEventTypeRepository = contEventTypeRepository;
        this.contEventLevelColorV2Service = contEventLevelColorV2Service;
    }


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


        @JsonIgnore
        public LocalDateTime getLastContEventTime() {
            return lastContEventTime;
        }

        @JsonIgnore
        public Date getLastContEventTimeDT() {
            return lastContEventTime != null ? LocalDateUtils.asDate(lastContEventTime) : null;
        }

        public String getLastContEventTimeStr() {
            return lastContEventTime != null ? lastContEventTime.format(formatter) : null;
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
    public List<ContObjectStats> loadMonitorData(Predicate<Long> contObjectFilter) {
        List<ContObjectStats> statsObjList = monitorWidgetRepository.findContObjectStats()
            .stream().map(ContObjectStats::new).filter(i -> contObjectFilter.test(i.contObjectId)).collect(Collectors.toList());

        List<Long> contEventTypeIds = statsObjList.stream().map(ContObjectStats::getContEventTypeId).distinct().collect(Collectors.toList());

        List<ContEventTypeDTO> types = contEventTypeRepository.selectContEventTypes(contEventTypeIds).stream().map(ContEventTypeDTO.MAPPER::toDto).collect(Collectors.toList());

        types.forEach(i -> i.setLevelColor(contEventLevelColorV2Service.getColorName(i.getContEventLevel())));

        Map<Long, ContEventTypeDTO> typeMap = types.stream().collect(Collectors.toMap(ContEventTypeDTO::getId, Function.identity()));
        statsObjList.forEach(i -> i.setContEventType(typeMap.get(i.getContEventTypeId())));
        return statsObjList;
    }


    /**
     *
     * @return
     */
    public List<ContObjectStats> loadMonitorData() {
        return loadMonitorData(i -> true);
    }

}
