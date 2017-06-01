package ru.excbt.datafuse.nmk.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataHWaterController;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataResource extends SubscrApiController {


    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataResource.class);

    private final SubscrContServiceDataHWaterController subscrContServiceDataHWaterController;


    private final WebAppPropsService webAppPropsService;

    public SubscrContServiceDataResource(WebAppPropsService webAppPropsService,
                                         SubscrContServiceDataHWaterController subscrContServiceDataHWaterController) {
        this.subscrContServiceDataHWaterController = subscrContServiceDataHWaterController;
        this.webAppPropsService = webAppPropsService;
    }

    @RequestMapping(value = "/service-data/cont-objects/import", method = RequestMethod.POST,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> importDataMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles) {


        // Check if it CSV files
        List<CsvUtils.CheckFileResult> checkFileResults = CsvUtils.checkCsvFiles(multipartFiles);
        List<CsvUtils.CheckFileResult> isNotPassed = checkFileResults.stream().filter((i) -> !i.isPassed()).collect(Collectors.toList());

        if (isNotPassed.size() > 0) {
            return responseBadRequest(ApiResult.badRequest(isNotPassed.stream().map((i) -> i.getErrorDesc()).collect(Collectors.toList())));
        }


        // See what is in the uploaded file

        List<String> fileHeaders = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            try {
                try {
                    if (multipartFile.getInputStream().markSupported()) {
                        multipartFile.getInputStream().mark(512);
                    }

                    InputStreamReader isr = new InputStreamReader(multipartFile.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    String header = reader.readLine();

                    log.debug("FILE_HEADER: {}", header);

                    fileHeaders.add(header);

                } finally {
                    if (multipartFile.getInputStream().markSupported()) {
                        multipartFile.getInputStream().reset();
                    }
                }

                log.info("MARK SUPPORTED:{}", multipartFile.getInputStream().markSupported());
            }
            catch (IOException e) {
                log.error("Internal server error: file upload: \n{}", e.getMessage());
                return responseInternalServerError(ApiResult.error(e, "Internal server error: file upload "));
            }
        }


        Set<String> headerSet = fileHeaders.stream().collect(Collectors.toSet());

        if (headerSet.size() > 1) {
            return responseBadRequest(ApiResult.badRequest("Одновременная загрузка разных файлов импорта не поддерживается"));
        }


        if (headerSet.contains(HWatersCsvService.CSV_HEADER)) {
            return subscrContServiceDataHWaterController.importDataHWaterMultipleFiles(multipartFiles);
        }

        return responseBadRequest();

    }

}
