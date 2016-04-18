package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Класс для доступа к источника данных
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
public class SubscrDataSourceController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDataSourceController.class);

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	@Autowired
	private DataSourceTypeRepository dataSourceTypeRepository;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataSources", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataSources() {
		List<SubscrDataSource> result = subscrDataSourceService.selectBySubscriber(getCurrentSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param subscrDataSourceId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataSource(@PathVariable("dataSourceId") Long dataSourceId) {
		SubscrDataSource result = subscrDataSourceService.findOne(dataSourceId);
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param subscrDataSource
	 * @return
	 */
	@RequestMapping(value = "/dataSources", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDataSource(@RequestBody SubscrDataSource subscrDataSource,
			HttpServletRequest request) {
		checkNotNull(subscrDataSource);

		if (subscrDataSource.getDataSourceTypeKey() == null) {
			return responseBadRequest(ApiResult.validationError("dataSourceKey is null"));
		}

		subscrDataSource.setSubscriberId(getCurrentSubscriberId());

		logger.trace("All Validation Passed");

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrDataSource, Long>(subscrDataSource,
				request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrDataSource processAndReturnResult() {
				return subscrDataSourceService.createOne(entity);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param subscrDataSourceId
	 * @param subscrDataSource
	 * @return
	 */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateDataSource(@PathVariable("dataSourceId") Long subscrDataSourceId,
			@RequestBody SubscrDataSource subscrDataSource) {
		checkNotNull(subscrDataSourceId);
		checkNotNull(subscrDataSource);
		checkArgument(!subscrDataSource.isNew());
		checkArgument(subscrDataSourceId.longValue() == subscrDataSource.getId().longValue());
		checkNotNull(subscrDataSource.getSubscriberId());

		ApiAction action = new AbstractEntityApiAction<SubscrDataSource>(subscrDataSource) {

			@Override
			public void process() {
				setResultEntity(subscrDataSourceService.updateOne(entity));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscrDataSourceId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteDataSource(@PathVariable("dataSourceId") Long subscrDataSourceId) {

		checkNotNull(subscrDataSourceId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrDataSourceService.deleteOne(subscrDataSourceId);

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataSourceTypes", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataSourceTypes() {
		List<DataSourceType> result = dataSourceTypeRepository.findAll();
		result.sort((a, b) -> Integer.compare(a.getTypeOrder(), b.getTypeOrder()));
		return responseOK(result);
	}

}
