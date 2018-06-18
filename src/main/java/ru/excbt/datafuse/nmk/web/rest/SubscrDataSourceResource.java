package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.RawModemModel;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrDataSourceDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.RawModemService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Comparator;
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
@RestController
@RequestMapping("/api/rma")
public class SubscrDataSourceResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDataSourceResource.class);

	private final SubscrDataSourceService subscrDataSourceService;

	private final DataSourceTypeRepository dataSourceTypeRepository;

	private final RawModemService rawModemService;

    private final PortalUserIdsService portalUserIdsService;

	@Autowired
    public SubscrDataSourceResource(SubscrDataSourceService subscrDataSourceService, DataSourceTypeRepository dataSourceTypeRepository, RawModemService rawModemService, PortalUserIdsService portalUserIdsService) {
        this.subscrDataSourceService = subscrDataSourceService;
        this.dataSourceTypeRepository = dataSourceTypeRepository;
        this.rawModemService = rawModemService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDataSources() {
		List<SubscrDataSourceDTO> result = subscrDataSourceService.selectDataSourceDTOBySubscriber((portalUserIdsService.getCurrentIds().getSubscriberId()));
		return ApiResponse.responseOK(result);
	}

    /**
     *
     * @param dataSourceId
     * @return
     */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> getDataSource(@PathVariable("dataSourceId") Long dataSourceId) {
		SubscrDataSourceDTO result = subscrDataSourceService.findOne(dataSourceId);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param subscrDataSource
	 * @return
	 */
	@RequestMapping(value = "/dataSources", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
	public ResponseEntity<?> createDataSource(@RequestBody SubscrDataSource subscrDataSource,
			HttpServletRequest request) {
		checkNotNull(subscrDataSource);

		if (subscrDataSource.getDataSourceTypeKey() == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("dataSourceKey is null"));
		}

		subscrDataSource.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());

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

		return ApiActionTool.processResponceApiActionCreate(action);
	}

	/**
	 *
	 * @param subscrDataSourceId
	 * @param subscrDataSource
	 * @return
	 */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
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

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscrDataSourceId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/{dataSourceId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> deleteDataSource(@PathVariable("dataSourceId") Long subscrDataSourceId) {

		checkNotNull(subscrDataSourceId);

		ApiAction action = (ApiActionAdapter) () -> subscrDataSourceService.deleteOne(subscrDataSourceId);

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSourceTypes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getDataSourceTypes() {
		List<DataSourceType> result = dataSourceTypeRepository.findAll();
		result.sort(Comparator.comparingInt(DataSourceType::getTypeOrder));
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getRawModemModel() {
		List<RawModemModel> resultList = rawModemService.selectRawModels();
		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param rawModemModelId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId) {
		RawModemModel resultList = rawModemService.selectRawModel(rawModemModelId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param rawModemModelId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> putRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId,
			@RequestBody RawModemModel requestEntity) {

		if (!SecurityUtils.isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<RawModemModel>(requestEntity) {

			@Override
			public RawModemModel processAndReturnResult() {

				return rawModemService.saveRawModemModel(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param requestEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<?> postRawModemModel(
			@RequestBody RawModemModel requestEntity, HttpServletRequest request) {

		if (!SecurityUtils.isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		if (requestEntity.getRawModemType() == null) {
			return ApiResponse.responseBadRequest();
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

		return ApiActionTool.processResponceApiActionCreate(action);

	}

	/**
	 *
	 * @param rawModemModelId
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/{rawModemModelId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> deleteRawModemModel(@PathVariable("rawModemModelId") Long rawModemModelId) {

		if (!SecurityUtils.isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		ApiAction action = (ApiActionAdapter) () -> rawModemService.deleteRawModemModel(rawModemModelId);

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/dataSources/rawModemModels/rawModemModelIdentity", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
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

		return ApiResponse.responseOK(result);
	}

}
