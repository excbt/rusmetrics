package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;
import ru.excbt.datafuse.nmk.data.service.TemperatureChartService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/api/rma")
public class TemperatureChartController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartController.class);

	@Autowired
	private TemperatureChartService temperatureChartService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTemperatureChartsAll() {
		List<TemperatureChart> resultList = temperatureChartService.selectTemperatureChartsInfo();
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

    /**
     *
     * @param requestEntity
     * @param request
     * @return
     */
	@RequestMapping(value = "/temperatureCharts", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> postTemperatureCharts(@RequestBody TemperatureChart requestEntity,
			HttpServletRequest request) {

		ApiActionLocation action = new ApiActionEntityLocationAdapter<TemperatureChart, Long>(requestEntity, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public TemperatureChart processAndReturnResult() {
				return temperatureChartService.saveTemperatureChart(entity);
			}
		};
		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 *
	 * @param temperatureChartId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putTemperatureCharts(@PathVariable("temperatureChartId") Long temperatureChartId,
			@RequestBody TemperatureChart requestEntity) {

		ApiAction action = new ApiActionEntityAdapter<TemperatureChart>(requestEntity) {
			@Override
			public TemperatureChart processAndReturnResult() {
				return temperatureChartService.saveTemperatureChart(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param temperatureChartId
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteTemperatureChart(@PathVariable("temperatureChartId") Long temperatureChartId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				temperatureChartService.deleteTemperatureChart(temperatureChartId);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

    /**
     *
     * @param temperatureChartId
     * @return
     */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTemperatureChart(@PathVariable("temperatureChartId") Long temperatureChartId) {
		TemperatureChart result = temperatureChartService.selectTemperatureChart(temperatureChartId);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param temperatureChartId
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}/items", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTemperatureChartItem(@PathVariable("temperatureChartId") Long temperatureChartId) {
		List<TemperatureChartItem> resultList = temperatureChartService.selectTemperatureChartItems(temperatureChartId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param temperatureChartId
	 * @param requestEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}/items", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> postTemperatureChartItem(@PathVariable("temperatureChartId") Long temperatureChartId,
			@RequestBody TemperatureChartItem requestEntity, HttpServletRequest request) {

		requestEntity.setTemperatureChartId(temperatureChartId);

		ApiActionLocation action = new ApiActionEntityLocationAdapter<TemperatureChartItem, Long>(requestEntity,
				request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public TemperatureChartItem processAndReturnResult() {
				return temperatureChartService.saveTemperatureChartItem(entity);
			}
		};
		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 *
	 * @param temperatureChartItemId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}/items/{temperatureChartItemId}",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putTemperatureChartItem(@PathVariable("temperatureChartId") Long temperatureChartId,
			@PathVariable("temperatureChartItemId") Long temperatureChartItemId,
			@RequestBody TemperatureChartItem requestEntity) {

		ApiAction action = new ApiActionEntityAdapter<TemperatureChartItem>(requestEntity) {
			@Override
			public TemperatureChartItem processAndReturnResult() {
				return temperatureChartService.saveTemperatureChartItem(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param temperatureChartId
	 * @param temperatureChartItemId
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/{temperatureChartId}/items/{temperatureChartItemId}",
			method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteTemperatureChartItem(@PathVariable("temperatureChartId") Long temperatureChartId,
			@PathVariable("temperatureChartItemId") Long temperatureChartItemId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				temperatureChartService.deleteTemperatureChartItem(temperatureChartItemId);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/byContZPoint/{contZPointId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTemperatureChartsByContZPointId(@PathVariable("contZPointId") Long contZPointId) {
		List<TemperatureChart> resultList = temperatureChartService.selectTemperatureChartsByContZPointId(contZPointId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/temperatureCharts/byContObject/{contObjectId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTemperatureChartsByContObjectId(@PathVariable("contObjectId") Long contObjectId) {
		List<TemperatureChart> resultList = temperatureChartService.selectTemperatureChartsByContObjectId(contObjectId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
