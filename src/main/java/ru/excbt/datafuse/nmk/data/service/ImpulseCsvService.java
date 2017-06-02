package ru.excbt.datafuse.nmk.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulseUCsv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulse_CsvFormat;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.TimeZoneService;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kovtonyk on 31.05.2017.
 */
@Service
public class ImpulseCsvService {
    private final TimeZoneService timeZoneService;

    public static final String CSV_HEADER = "comment,login,dataDate,dataValue";

    public static final String FILE_STARTS = "icl";

    public ImpulseCsvService(TimeZoneService timeZoneService) {
        this.timeZoneService = timeZoneService;
    }

    /**
     *
     * @param contServiceDataImpulseList
     * @return
     * @throws JsonProcessingException
     */
    public byte[] writeDataToCsv(List<ContServiceDataImpulse> contServiceDataImpulseList)
        throws JsonProcessingException {
        checkNotNull(contServiceDataImpulseList);

        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();

        mapper.addMixIn(ContServiceDataImpulse.class, ContServiceDataImpulse_CsvFormat.class);

        mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

        CsvSchema schema = mapper.schemaFor(ContServiceDataImpulse.class).withHeader();

        byte[] byteArray = mapper.writer(schema).writeValueAsBytes(contServiceDataImpulseList);

        return byteArray;
    }

    /**
     *
     * @param csvUList
     * @return
     * @throws JsonProcessingException
     */
    public byte[] writeDataToUCsv(List<ContServiceDataImpulseUCsv> csvUList)
        throws JsonProcessingException {
        checkNotNull(csvUList);

        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();

        mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

        CsvSchema schema = mapper.schemaFor(ContServiceDataImpulseUCsv.class).withHeader();

        byte[] byteArray = mapper.writer(schema).writeValueAsBytes(csvUList);

        return byteArray;
    }

    public static boolean fileStarts(String fileName) {
        return CsvUtils.extractFileName(fileName).toLowerCase().startsWith(FILE_STARTS.toLowerCase());
    }

}
