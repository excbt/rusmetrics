package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.service.support.BenchmarkService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 * Контроллер для замера отклика системы
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.11.2015
 *
 */
@Controller
@RequestMapping("/api/benchmark")
public class BenchmarkController extends AbstractApiResource {

	@Autowired
	private BenchmarkService benchmarkService;

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/init/{subscriberId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> initBenchmark(@PathVariable("subscriberId") Long subscriberId) {
		try {
			benchmarkService.setBenchmarkSubscriberId(subscriberId);
		} catch (Exception e) {
			benchmarkService.reset();
			return ApiResponse.responseBadRequest(ApiResult.validationError("Init Benchmark fail"));
		}

		return ApiResponse.responseOK(ApiResult.ok("Init Benchmark successfully"));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus() {
		Long subscriberId = null;
		try {
			subscriberId = benchmarkService.getBenchmarkSubscriberId();
		} catch (Exception e) {
			return ApiResponse.responseBadRequest(ApiResult.invalidState("Benchmark is not init"));
		}

		return ApiResponse.responseOK(ApiResult.ok("Benchmark init to :" + subscriberId));
	}

}
