package ru.excbt.datafuse.nmk.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.excbt.datafuse.nmk.data.service.ImpulseCsvService;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataHWaterController;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataResource extends AbstractSubscrApiResource {


    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataResource.class);

    private final WebAppPropsService webAppPropsService;

    private final SubscrContServiceDataHWaterController subscrContServiceDataHWaterController;
    private final SubscrContServiceDataImpulseResource subscrContServiceDataImpulseController;


    public SubscrContServiceDataResource(WebAppPropsService webAppPropsService,
                                         SubscrContServiceDataHWaterController subscrContServiceDataHWaterController,
                                         SubscrContServiceDataImpulseResource subscrContServiceDataImpulseController) {
        this.webAppPropsService = webAppPropsService;
        this.subscrContServiceDataHWaterController = subscrContServiceDataHWaterController;
        this.subscrContServiceDataImpulseController = subscrContServiceDataImpulseController;

    }


    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String readHeaders(InputStream inputStream) throws IOException {
        String header = null;
        try {
            if (inputStream.markSupported()) {

                inputStream.mark(512);
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                header = reader.readLine();
                log.debug("FILE_HEADER: {}", header);
            } else {
                log.warn("MARK IS NOT SUPPORTED:");
            }

        } finally {
            if (inputStream.markSupported()) {
                inputStream.reset();
            }
        }
        return header;
    }


    /**
     *
     * @param multipartFiles
     * @return
     */
    @RequestMapping(value = "/service-data/cont-objects/import", method = RequestMethod.POST,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> importDataMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles) {


        // Check if it CSV files
        List<CsvUtils.CheckFileResult> checkFileResults = CsvUtils.checkCsvFiles(multipartFiles);
        List<CsvUtils.CheckFileResult> isNotPassed = checkFileResults.stream().filter((i) -> !i.isPassed()).collect(Collectors.toList());

        if (isNotPassed.size() > 0) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest(isNotPassed.stream().map((i) -> i.getErrorDesc()).collect(Collectors.toList())));
        }

        // See what is in the uploaded file

        Set<String> fileHeaders = new HashSet<>();

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                String header = readHeaders(multipartFile.getInputStream());
                if (header != null && !header.isEmpty()) {
                    fileHeaders.add(header);
                    continue;
                }

                if (ImpulseCsvService.fileStarts(multipartFile.getOriginalFilename())) {
                    fileHeaders.add(ImpulseCsvService.CSV_HEADER);
                } else {
                    fileHeaders.add(HWatersCsvService.CSV_HEADER);
                }


            }
            catch (IOException e) {
                log.error("Internal server error: file upload: \n{}", e.getMessage());
                return ApiResponse.responseInternalServerError(ApiResult.error(e, "Internal server error: file upload "));
            }
        }


        if (fileHeaders.size() > 1) {
            return ApiResponse.responseBadRequest(ApiResult.badRequest("Одновременная загрузка разных файлов импорта не поддерживается"));
        }


        if (fileHeaders.contains(HWatersCsvService.CSV_HEADER)) {
            return subscrContServiceDataHWaterController.importDataHWaterMultipleFiles(multipartFiles);
        }

        if (fileHeaders.contains(ImpulseCsvService.CSV_HEADER)) {
            return subscrContServiceDataImpulseController.importDataImpulseMultipleFilesCl(multipartFiles);
        }



        //return responseBadRequest();
        return subscrContServiceDataHWaterController.importDataHWaterMultipleFiles(multipartFiles);

    }

}
