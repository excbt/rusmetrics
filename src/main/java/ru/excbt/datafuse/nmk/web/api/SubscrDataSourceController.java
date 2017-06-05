package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.RawModemModel;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.RawModemService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Класс для доступа к источника данных
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
public class SubscrDataSourceController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDataSourceController.class);

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	@Autowired
	private DataSourceTypeRepository dataSourceTypeRepository;

	@Autowired
	private RawModemService rawModemService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataSources() {
		List<SubscrDataSource> result = subscrDataSourceService.selectDataSourceBySubscriber(getCurrentSubscriberId());
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

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getRawModemModel() {
		List<RawModemModel> resultList = rawModemService.selectRawModels();
		return responseOK(resultList);
	}

	/**
	 *
	 * @param rawModemModelId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId) {
		RawModemModel resultList = rawModemService.selectRawModel(rawModemModelId);
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param rawModemModelId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId,
			@RequestBody RawModemModel requestEntity) {

		if (!isSystemUser()) {
			return responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<RawModemModel>(requestEntity) {

			@Override
			public RawModemModel processAndReturnResult() {

				return rawModemService.saveRawModemModel(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param requestEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels", method = RequestMethod.POST)
	public ResponseEntity<?> postRawModemModel(
			@RequestBody RawModemModel requestEntity, HttpServletRequest request) {

		if (!isSystemUser()) {
			return responseForbidden();
		}

		if (requestEntity.getRawModemType() == null) {
			return responseBadRequest();
		}

		ApiActionLocation action = new ApiActionEntityLocationAdapter<RawModemModel, Long>(requestEntity, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public RawModemModel processAndReturnResult() {
				return rawModemService.saveRawModemModel(entity);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 *
	 * @param rawModemModelId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId) {

		if (!isSystemUser()) {
			return responseForbidden();
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				rawModemService.deleteRawModemModel(rawModemModelId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/rawModemModelIdentity", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getRawModemIdentity() {

		class RawModemIdentity {
			private final String keyname;
			private final String caption;

			public RawModemIdentity(String... args) {
				this.keyname = args[0];
				this.caption = args[1];
			}

			public String getKeyname() {
				return keyname;
			}

			public String getCaption() {
				return caption;
			}
		}

		RawModemIdentity serial = new RawModemIdentity("SERIAL_NR", "Серийный номер");
		RawModemIdentity macImei = new RawModemIdentity("IMEI", "IMEI");
		RawModemIdentity macAddr = new RawModemIdentity("MAC_ADDR", "MAC адрес");

		List<RawModemIdentity> result = Arrays.asList(serial, macImei, macAddr);

		return responseOK(result);
	}

}
