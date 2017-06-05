/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.fasterxml.uuid.Generators;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataImpulseService;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.utils.FileWriterUtils;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.RequestPageDataSelector;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractContServiceDataResource;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataImpulseController extends AbstractContServiceDataResource {

    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataImpulseController.class);

	private final ContServiceDataImpulseService contServiceDataImpulseService;

	private final WebAppPropsService webAppPropsService;

	@Autowired
    public SubscrContServiceDataImpulseController(ContServiceDataImpulseService contServiceDataImpulseService, WebAppPropsService webAppPropsService) {
        this.contServiceDataImpulseService = contServiceDataImpulseService;
        this.webAppPropsService = webAppPropsService;
    }

    /**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataDateSort
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceImpulse/{timeDetailType}/{contZPointId}/paged",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataImpulsePaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		RequestPageDataSelector<ContServiceDataImpulse> dataSelector = new RequestPageDataSelector<ContServiceDataImpulse>() {

			@Override
			public Page<ContServiceDataImpulse> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				checkNotNull(pageRequest);
				return contServiceDataImpulseService.selectImpulseByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, dataSelector);

		return resultResponse;

	}


    /**
     *
     * @param multipartFiles
     * @return
     */
    @RequestMapping(value = "/service/serviceImpulse/contObjects/importData-cl", method = RequestMethod.POST,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> importDataImpulseMultipleFilesCl(@RequestParam("files") MultipartFile[] multipartFiles) {

        checkNotNull(multipartFiles);

        if (multipartFiles.length == 0) {
            return responseBadRequest();
        }

        SubscriberParam subscriberParam = getSubscriberParam();

        List<CsvUtils.CheckFileResult> checkFileResults = CsvUtils.checkCsvFiles(multipartFiles);
        List<CsvUtils.CheckFileResult> isNotPassed = checkFileResults.stream().filter((i) -> !i.isPassed()).collect(Collectors.toList());

        if (isNotPassed.size() > 0) {
            return responseBadRequest(ApiResult.badRequest(isNotPassed.stream().map((i) -> i.getErrorDesc()).collect(Collectors.toList())));
        }

        // Processing files

        UUID trxId = Generators.timeBasedGenerator().generate();

        List<FileImportInfo> fileImportInfos = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            String fileName = FilenameUtils.getName(multipartFile.getOriginalFilename());

            String internalFilename = webAppPropsService.getSubscriberCsvPath(subscriberParam.getSubscriberId(),
                subscriberParam.getSubscrUserId(), trxId.toString().substring(30, 36) + '_' + fileName);

            File inFile = new File(internalFilename);

            try {
                @SuppressWarnings("unused")
                String digestMD5 = FileWriterUtils.writeFile(multipartFile.getInputStream(), inFile);
            } catch (IOException e) {
                log.error("Exception:{}", e);
                return responseInternalServerError(ApiResult.error(e));
            }

            FileImportInfo fileImportInfo = FileImportInfo.builder().internalFileName(internalFilename)
                .userFileName(fileName).build();

            fileImportInfos.add(fileImportInfo);

        }

        contServiceDataImpulseService.submitImportTask(getCurrentSubscUserId(), fileImportInfos);

	    return responseOK();
    }

}
