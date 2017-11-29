package ru.excbt.datafuse.nmk.web.rest.support;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kovtonyk on 05.06.2017.
 */
public class ApiResponse {
    /**
     *
     * @return
     */
    public static ResponseEntity<?> responseForbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.build(ApiResultCode.ERR_ACCESS_DENIED));
    }

    /**
     *
     * @return
     */
    public static ResponseEntity<?> responseNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     *
     * @return
     */
    public static ResponseEntity<?> responseNoContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     *
     * @return
     */
    public static ResponseEntity<?> responseBadRequest() {
        return ResponseEntity.badRequest().build();
    }

    /**
	 *
	 * @return
	 */
	public static ResponseEntity<?> responseOK() {
		return ResponseEntity.ok().build();
	}

    /**
	 *
	 * @return
	 */
	public static <T> ResponseEntity<?> responseOK(final ApiActionProcess<T> actionProcess) {
		return ApiActionTool.processResponceApiActionOk(actionProcess);
	}

    /**
	 *
	 * @param actionProcess
	 * @param extraCheck
	 * @return
	 */
	public static <T> ResponseEntity<?> responseOK(final ApiActionProcess<T> actionProcess,
                                                   Function<T, ResponseEntity<?>> extraCheck) {
		return ApiActionTool.processResponceApiActionOk(actionProcess, extraCheck);
	}

    /**
	 *
	 * @return
	 */
	public static ResponseEntity<?> responseOK(Object body) {
		return ResponseEntity.ok(body);
	}

    /**
	 *
	 * @return
	 */
	public static ResponseEntity<?> responseContent(Optional<?> body) {
		return body != null ? ResponseEntity.ok(body) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

    /**
	 *
	 * @param apiResult
	 * @return
	 */
	public static ResponseEntity<?> responseOK(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.OK).body(apiResult);
	}

    /**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> responseUpdate(final ApiActionProcess<T> actionProcess) {
		return ApiActionTool.processResponceApiActionUpdate(actionProcess);
	}

    /**
	 *
	 * @param actionProcess
	 * @param extraCheck
	 * @return
	 */
	public static <T> ResponseEntity<?> responseUpdate(final ApiActionProcess<T> actionProcess,
                                                       Function<T, ResponseEntity<?>> extraCheck) {
		return ApiActionTool.processResponceApiActionUpdate(actionProcess, extraCheck);
	}

    /**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T extends ModelIdable<K>, K extends Serializable> ResponseEntity<?> responseCreate(
        final ApiActionProcess<T> actionProcess, final Supplier<String> uriLocationSupplier) {
		return ApiActionTool.processResponceApiActionCreate(actionProcess, uriLocationSupplier);

	}

    /**
     *
     * @param body
     * @return
     */
    public static ResponseEntity<?> responseCreated(Object body) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.CREATED);
        if (body != null) {
            return bodyBuilder.body(body);
        }
        return bodyBuilder.build();
    }


    /**
	 *
	 * @return
	 */
	public static <T> ResponseEntity<?> responseDelete(final ApiActionProcess<T> actionProcess) {
		return ApiActionTool.processResponceApiActionDelete(actionProcess);
	}

    /**
	 *
	 * @param apiResult
	 * @return
	 */
	public static ResponseEntity<?> responseBadRequest(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResult);
	}

    /**
	 *
	 * @param apiResult
	 * @return
	 */
	public static ResponseEntity<?> responseInternalServerError(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResult);
	}

    /**
     *
     * @return
     */
	public static ResponseEntity<?> responseInternalServerError() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

    /**
	 *
	 * @param is
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	public static ResponseEntity<?> processDownloadInputStream(InputStream is, MediaType mediaType, long contentLength,
                                                               String filename) {
		checkNotNull(is);

		InputStreamResource isr = new InputStreamResource(is);
		return processDownloadResource(isr, mediaType, contentLength, filename);
	}

    /**
	 *
	 * @param is
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @param makeAttach
	 * @return
	 */
	public static ResponseEntity<?> processDownloadInputStream(InputStream is, MediaType mediaType, long contentLength,
                                                               String filename, boolean makeAttach) {
		checkNotNull(is);

		InputStreamResource isr = new InputStreamResource(is);
		return processDownloadResource(isr, mediaType, contentLength, filename, makeAttach);
	}

    /**
     *
     * @param file
     * @param mediaType
     * @return
     */
	public static ResponseEntity<?> processDownloadFile(File file, MediaType mediaType) {
		checkNotNull(file);
		FileSystemResource fsr = new FileSystemResource(file);
		return processDownloadResource(fsr, mediaType, file.length(), file.getName());
	}

    /**
	 *
	 * @param resource
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	public static ResponseEntity<?> processDownloadResource(Resource resource, MediaType mediaType, long contentLength,
                                                            String filename, boolean makeAttach) {

		checkNotNull(resource);
		checkNotNull(mediaType);
		checkNotNull(filename);

		// set content attributes for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setContentLength(contentLength);

		if (makeAttach) {
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", filename);
			headers.set(headerKey, headerValue);
		}

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

    /**
     *
     * @param datePeriodParser
     * @return
     */
	public static ResponseEntity<?> checkDatePeriodArguments(LocalDatePeriodParser datePeriodParser) {
		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters dateFrom:{} and dateTo:{}",
							datePeriodParser.getParserArguments().dateFromStr,
							datePeriodParser.getParserArguments().dateToStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters dateFrom:{} is greater than dateTo:{}",
							datePeriodParser.getParserArguments().dateFromStr,
							datePeriodParser.getParserArguments().dateToStr));
		}

		return null;
	}

    /**
	 *
	 * @param resource
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	public static ResponseEntity<?> processDownloadResource(Resource resource, MediaType mediaType, long contentLength,
                                                            String filename) {

		return processDownloadResource(resource, mediaType, contentLength, filename, true);
	}
}
