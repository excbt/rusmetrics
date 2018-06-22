package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.*;
import ru.excbt.datafuse.nmk.data.model.types.DataSourceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.service.mapper.SubscrDataSourceMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;

/**
 * Сервис для работы с приборами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.05.2015
 *
 */
@Service
public class DeviceObjectService implements SecuredRoles {

    private static final Logger log = LoggerFactory.getLogger(DeviceObjectService.class);

	private final DeviceObjectRepository deviceObjectRepository;

	private final DeviceModelService deviceModelService;

	private final DeviceObjectMetaVzletRepository deviceObjectMetaVzletRepository;

	private final DeviceObjectDataSourceService deviceObjectDataSourceService;

	private final DeviceObjectDataSource2Repository deviceObjectDataSource2Repository;

	private final DeviceObjectMetadataService deviceObjectMetadataService;

	private final DeviceMetadataService deviceMetadataService;

	private final DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	//private final V_DeviceObjectTimeOffsetRepository deviceObjectTimeOffsetRepository;

	private final DeviceObjectMapper deviceObjectMapper;

	private final SubscrDataSourceRepository subscrDataSourceRepository;

	private final ObjectAccessService objectAccessService;

    private final SubscrDataSourceMapper subscrDataSourceMapper;

    @Autowired
    public DeviceObjectService(DeviceObjectRepository deviceObjectRepository,
                               DeviceModelService deviceModelService,
                               DeviceObjectMetaVzletRepository deviceObjectMetaVzletRepository,
                               DeviceObjectDataSourceService deviceObjectDataSourceService,
                               DeviceObjectDataSource2Repository deviceObjectDataSource2Repository,
                               DeviceObjectMetadataService deviceObjectMetadataService,
                               DeviceMetadataService deviceMetadataService,
                               DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService,
                               //V_DeviceObjectTimeOffsetRepository deviceObjectTimeOffsetRepository,
                               DeviceObjectMapper deviceObjectMapper, SubscrDataSourceRepository subscrDataSourceRepository, ObjectAccessService objectAccessService, SubscrDataSourceMapper subscrDataSourceMapper) {
        this.deviceObjectRepository = deviceObjectRepository;
        this.deviceModelService = deviceModelService;
        this.deviceObjectMetaVzletRepository = deviceObjectMetaVzletRepository;
        this.deviceObjectDataSourceService = deviceObjectDataSourceService;
        this.deviceObjectDataSource2Repository = deviceObjectDataSource2Repository;
        this.deviceObjectMetadataService = deviceObjectMetadataService;
        this.deviceMetadataService = deviceMetadataService;
        this.deviceObjectLoadingSettingsService = deviceObjectLoadingSettingsService;
        //this.deviceObjectTimeOffsetRepository = deviceObjectTimeOffsetRepository;
        this.deviceObjectMapper = deviceObjectMapper;
        this.subscrDataSourceRepository = subscrDataSourceRepository;
        this.objectAccessService = objectAccessService;
        this.subscrDataSourceMapper = subscrDataSourceMapper;
    }


    /**
     *
     * @param contObjectId
     * @param deviceObject
     * @return
     */
    @Transactional
    @Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
    public DeviceObject automationCreate (Long contObjectId, DeviceObject deviceObject) {

        deviceObject.setContObject(new ContObject().id(contObjectId));
        DeviceModel deviceModel = deviceModelService.findDeviceModel(deviceObject.getDeviceModelId());
        deviceObject.setDeviceModel(deviceModel);

        ActiveDataSourceInfoDTO dsi = deviceObject.getEditDataSourceInfo();

        DeviceObjectDataSource2 deviceObjectDataSource = (dsi == null || dsi.getSubscrDataSourceId() == null) ? null
            : new DeviceObjectDataSource2();

        if (deviceObjectDataSource != null) {
            initDeviceObjectDataSource(dsi, deviceObjectDataSource);
            deviceObjectDataSource.deviceObjectId(deviceObject.getId());
        }
        deviceObject.saveDeviceObjectCredentials();

        return saveDeviceObject(deviceObject, deviceObjectDataSource);
    }

    /**
     *
     * @param deviceObjectDTO
     * @return
     */
    @Transactional
    @Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
    public DeviceObject automationCreate (DeviceObjectDTO deviceObjectDTO) {

        DeviceObject deviceObject = deviceObjectMapper.toEntity(deviceObjectDTO);

        ActiveDataSourceInfoDTO dsi = deviceObject.getEditDataSourceInfo();

        DeviceObjectDataSource2 deviceObjectDataSource = (dsi == null || dsi.getSubscrDataSourceId() == null) ? null
            : new DeviceObjectDataSource2();

        initDeviceObjectDataSource(dsi, deviceObjectDataSource);

        deviceObject.saveDeviceObjectCredentials();

        return saveDeviceObject(deviceObject, deviceObjectDataSource);
    }

    /*

     */
    @Transactional
    @Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
    public DeviceObject automationUpdate(Long contObjectId, DeviceObject deviceObject) {
        deviceObject.setContObject(new ContObject().id(contObjectId));
        deviceObject.setDeviceModel(new DeviceModel().id(deviceObject.getDeviceModelId()));

        ActiveDataSourceInfoDTO dsi = deviceObject.getEditDataSourceInfo();

        DeviceObjectDataSource2 deviceObjectDataSource = (dsi == null || dsi.getSubscrDataSourceId() == null) ? null
            : new DeviceObjectDataSource2();

        initDeviceObjectDataSource(dsi, deviceObjectDataSource);

        deviceObject.saveDeviceObjectCredentials();

        DeviceObject result = saveDeviceObject(deviceObject, deviceObjectDataSource);
        //result.shareDeviceLoginInfo();
        return result;
    }

    @Transactional
    @Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
    public DeviceObject automationUpdate(Long contObjectId, DeviceObjectDTO deviceObjectDTO) {

        DeviceObject deviceObject = deviceObjectMapper.toEntity(deviceObjectDTO);

        ActiveDataSourceInfoDTO dsi = deviceObject.getEditDataSourceInfo();

        DeviceObjectDataSource2 deviceObjectDataSource = (dsi == null || dsi.getSubscrDataSourceId() == null) ? null
            : new DeviceObjectDataSource2();

        initDeviceObjectDataSource(dsi, deviceObjectDataSource);

        deviceObject.saveDeviceObjectCredentials();

        DeviceObject result = saveDeviceObject(deviceObject, deviceObjectDataSource);
        if (deviceObjectDataSource != null) {
            deviceObjectDataSource.setDeviceObjectId(result.getId());
            deviceObjectDataSourceService.saveDeviceDataSource2(deviceObjectDataSource);
        }

        //result.shareDeviceLoginInfo();
        return result;
    }


    /*

     */
    private void initDeviceObjectDataSource(ActiveDataSourceInfoDTO dsi, DeviceObjectDataSource2 deviceObjectDataSource) {
        if (deviceObjectDataSource != null && dsi != null) {
            SubscrDataSource ds = subscrDataSourceRepository.findOne(dsi.getSubscrDataSourceId());
            if (ds == null) {
                throw DBExceptionUtil.newEntityNotFoundException(SubscrDataSource.class, dsi.getSubscrDataSourceId());
            }
            deviceObjectDataSource.setSubscrDataSource(ds);
            deviceObjectDataSource.setSubscrDataSourceAddr(dsi.getSubscrDataSourceAddr());
            deviceObjectDataSource.setDataSourceTable(dsi.getDataSourceTable());
            deviceObjectDataSource.setDataSourceTable1h(dsi.getDataSourceTable1h());
            deviceObjectDataSource.setDataSourceTable24h(dsi.getDataSourceTable24h());
        }
    }


    /**
	 *
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject createManualDeviceObject() {

		DeviceObject deviceObject = new DeviceObject();
		DeviceModel deviceModel = deviceModelService.findPortalDeviceModel();
		checkNotNull(deviceModel, "DeviceModel of Portal is not found");

		deviceObject.setDeviceModel(deviceModel);
		deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	/**
	 *
	 * @param deviceObject
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject createManualDeviceObject(DeviceObject deviceObject) {
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkArgument(deviceObject.isNew());
		checkNotNull(deviceObject.getDeviceModel(), "Device Model is NULL");
		deviceObject.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	/**
	 *
	 * @param deviceObjectId
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteManualDeviceObject(Long deviceObjectId) {
		DeviceObject deviceObject = selectDeviceObject(deviceObjectId);
		if (ExSystemKey.MANUAL.isNotEquals(deviceObject.getExSystemKeyname())) {
			throw new PersistenceException(
					String.format("Delete DeviceObject(id=%d) with exSystem=%s is not supported ", deviceObjectId,
							deviceObject.getExSystemKeyname()));
		}
		deviceObjectRepository.delete(deviceObjectId);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<DeviceObject> selectDeviceObjectsByContObjectId(Long contObjectId) {
		List<DeviceObject> resultList = deviceObjectRepository.selectDeviceObjectsByContObjectId(contObjectId);
//		resultList.forEach(i -> {
//			i.loadLazyProps();
//		});
		return resultList;
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional( readOnly = true)
	public DeviceObjectMetaVzlet selectDeviceObjectMetaVzlet(Long deviceObjectId) {
		List<DeviceObjectMetaVzlet> vList = deviceObjectMetaVzletRepository.findByDeviceObjectId(deviceObjectId);

		DeviceObjectMetaVzlet result = vList.size() > 0 ? vList.get(0) : null;
		if (result != null) {
			result.getVzletSystem1();
			result.getVzletSystem2();
			result.getVzletSystem3();
		}
		return result;
	}

	/**
	 *
	 * @param deviceObjectMetaVzlet
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObjectMetaVzlet updateDeviceObjectMetaVzlet(DeviceObjectMetaVzlet deviceObjectMetaVzlet) {
		checkNotNull(deviceObjectMetaVzlet);
		if (deviceObjectMetaVzlet.getExcludeNulls() == null) {
			deviceObjectMetaVzlet.setExcludeNulls(false);
		}
		if (deviceObjectMetaVzlet.getMetaPropsOnly() == null) {
			deviceObjectMetaVzlet.setMetaPropsOnly(false);
		}
		return deviceObjectMetaVzletRepository.save(deviceObjectMetaVzlet);
	}

    /**
     *
     * @param deviceObjectId
     */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObjectMetaVzlet(Long deviceObjectId) {
		checkNotNull(deviceObjectId);

		DeviceObjectMetaVzlet entity = selectDeviceObjectMetaVzlet(deviceObjectId);
		if (entity != null) {
			deviceObjectMetaVzletRepository.delete(entity);
		}
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	//@Transactional( readOnly = true)
	private List<DeviceObjectMetaVzlet> findDeviceObjectMetaVzlet(long deviceObjectId) {
		return deviceObjectMetaVzletRepository.findByDeviceObjectId(deviceObjectId);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public DeviceObject selectDeviceObject(long id) {
		DeviceObject result = deviceObjectRepository.findOne(id);
//		if (result != null) {
//			result.loadLazyProps();
//		}
		return result;
	}

	@Transactional( readOnly = true)
	public DeviceObjectFullVM selectDeviceObjectFullVM(long id) {
		DeviceObject result = deviceObjectRepository.findOne(id);
		return deviceObjectMapper.toFullVM(result);
	}

	@Transactional( readOnly = true)
	public DeviceObjectFullVM selectDeviceObjectFullVM_Rma(long id) {
		DeviceObject result = deviceObjectRepository.findOne(id);
		return deviceObjectMapper.toFullVM(result).shareDeviceLoginInfo(result);
	}

	/**
	 *
	 * @param deviceObject
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveDeviceObject(DeviceObject deviceObject) {
		return saveDeviceObject(deviceObject, null);
	}

	/**
	 *
	 * @param deviceObject
	 * @param deviceObjectDataSource
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveDeviceObject(DeviceObject deviceObject, DeviceObjectDataSource2 deviceObjectDataSource) {
		// Checking
		checkNotNull(deviceObject, "Argument DeviceObject is NULL");
		checkNotNull(deviceObject.getDeviceModel(), "Device Model is NULL");
		if (deviceObjectDataSource != null) {
			checkNotNull(deviceObjectDataSource.getSubscrDataSource());
		}
		// Set manual flag
		deviceObject.setIsManual(true);

		DeviceObject oldDeviceObject = deviceObject.isNew() ? null
				: deviceObjectRepository.findOne(deviceObject.getId());

		boolean isNew = deviceObject.isNew();
		if (deviceObject.getDeviceObjectLastInfo() != null) {
			deviceObject.getDeviceObjectLastInfo().setDeviceObject(deviceObject);
		}

		if (deviceObject.getHeatRadiatorTypeId() != null) {
		    deviceObject.setHeatRadiatorType(new HeatRadiatorType().id(deviceObject.getHeatRadiatorTypeId()));
        }

		DeviceObject savedDeviceObject = deviceObjectRepository.saveAndFlush(deviceObject);

		if (deviceObjectDataSource != null) {
			deviceObjectDataSource.setDeviceObjectId(savedDeviceObject.getId());
			DeviceObjectDataSource2 resultDataSource = deviceObjectDataSourceService
					.saveDeviceDataSource2(deviceObjectDataSource);
			savedDeviceObject.setDeviceObjectDataSource(resultDataSource);

			if (ExSystemKey.VZLET.getKeyname()
					.equals(resultDataSource.getSubscrDataSource().getDataSourceType().getKeyname())) {

			}

		}

		DeviceModel deviceModel = deviceModelService.findDeviceModel(deviceObject.getDeviceModelId());

		// TODO Metadata form Impulse Devices
		// Проверяем источник данных для прямой загрузки, за исключением импульсных приборов
		if (!Boolean.TRUE.equals(deviceModel.getIsImpulse()) && deviceObjectDataSource != null
				&& deviceObjectDataSource.getSubscrDataSource() != null && DataSourceTypeKey.DEVICE
						.equalsKeyname(deviceObjectDataSource.getSubscrDataSource().getDataSourceTypeKey())) {

			if (oldDeviceObject == null
					|| !oldDeviceObject.getDeviceModelId().equals(deviceObject.getDeviceModelId())) {
				List<DeviceMetadata> deviceMetadataList = deviceMetadataService.selectDeviceMetadata(
						deviceObject.getDeviceModelId(), DeviceMetadataService.DEVICE_METADATA_TYPE);
				// Если изменилась модель, а новых типовых метаданных нет, то бросаем exception
				if (deviceMetadataList.size() != 0) {
					// Удаляем текущие метаданные
					deviceObjectMetadataService.deleteDeviceObjectMetadata(deviceObject.getId());
					// Заполняем типовые метаданные
					deviceObjectMetadataService.copyDeviceMetadata(deviceObject.getDeviceModelId(),
							deviceObject.getId());
				} else {
					//					throw new PersistenceException(String.format("DeviceMetadata for DeviceModel (id=%d) is not found",
					//					deviceObject.getDeviceModelId()));
				}

			}

		}

		if (deviceObjectDataSource != null && deviceObjectDataSource.getSubscrDataSource() != null
				&& DataSourceTypeKey.VZLET
						.equalsKeyname(deviceObjectDataSource.getSubscrDataSource().getDataSourceTypeKey())) {
			deviceObjectMetaVzletInit(savedDeviceObject);
		}

		// DeviceObjectLoadingSettings create new
		if (isNew) {
			DeviceObjectLoadingSettings deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
					.newDefaultDeviceObjectLoadingSettings(savedDeviceObject);
			deviceObjectLoadingSettingsService.saveOne(deviceObjectLoadingSettings);
		} else {
			DeviceObjectLoadingSettings deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
					.getDeviceObjectLoadingSettings(savedDeviceObject);
			if (deviceObjectLoadingSettings.isNew()) {
				deviceObjectLoadingSettings = deviceObjectLoadingSettingsService
						.newDefaultDeviceObjectLoadingSettings(savedDeviceObject);
				deviceObjectLoadingSettingsService.saveOne(deviceObjectLoadingSettings);
			}
		}

		DeviceObject result = selectDeviceObject(savedDeviceObject.getId());

		return result;
	}

	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObject saveDeviceObjectShort(DeviceObjectDTO deviceObjectDTO) {
		// Checking
		checkNotNull(deviceObjectDTO, "Argument DeviceObject is NULL");
		//checkNotNull(deviceObjectDTO.getDeviceModelId(), "Device Model is NULL");

		DeviceObject deviceObject = deviceObjectDTO.isNew() ? null
				: selectDeviceObject(deviceObjectDTO.getId());

		checkNotNull(deviceObject);

		deviceObjectMapper.updateDeviceObjectFromDto(deviceObjectDTO, deviceObject);

		if (deviceObjectDTO.getDeviceLoginInfo() != null && SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
		    deviceObject.setDevicePassword(deviceObjectDTO.getDeviceLoginInfo().getDevicePassword());
		    deviceObject.setDeviceLogin(deviceObjectDTO.getDeviceLoginInfo().getDeviceLogin());
        }

		DeviceObject savedDeviceObject = deviceObjectRepository.saveAndFlush(deviceObject);

		DeviceObjectDataSource2 deviceObjectDataSource = deviceObject.getActiveDataSource();



		//Optional<EditDataSourceDTO> optionalDataSource =
        Optional.ofNullable(deviceObjectDTO.getEditDataSourceInfo())
                .filter(i -> Objects.nonNull(i.getSubscrDataSourceAddr()))
                .filter(i -> Objects.nonNull(i.getSubscrDataSourceId()))
                .filter(i -> Objects.nonNull(deviceObjectDataSource))
                .filter(dto -> deviceObjectDataSource != null &&
                            dto.getId().equals(deviceObjectDataSource.getSubscrDataSource().getId()))
            .ifPresent(i -> {
                deviceObjectDataSource.setSubscrDataSourceAddr(i.getSubscrDataSourceAddr());
                deviceObjectDataSource2Repository.save(deviceObjectDataSource);
            });

        //Optional<DeviceObjectDataSource2> optionalDataSource =

//        if (deviceObjectDataSource != null &&
//            deviceObjectDTO.getEditDataSourceInfo() != null &&
//            deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceId() != null &&
//                deviceObjectDataSource.getSubscrDataSource().getId().equals(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceId())) {
//
//            if (deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceAddr() != null)
//                deviceObjectDataSource.setSubscrDataSourceAddr(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceAddr());
//
//            deviceObjectDataSourceRepository.save(deviceObjectDataSource);
//        }


        //DeviceObject result = selectDeviceObject(deviceObject.getId());

//        if (SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
//            result.shareDeviceLoginInfo();
//        }


        return savedDeviceObject;
	}


	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN, ROLE_SUBSCR_ADMIN })
	public DeviceObject saveDeviceObjectDTO_lvlS1(DeviceObjectDTO deviceObjectDTO) {
		// Checking
		checkNotNull(deviceObjectDTO, "Argument DeviceObject is NULL");

		DeviceObject deviceObject = deviceObjectDTO.isNew() ? null
				: selectDeviceObject(deviceObjectDTO.getId());

		checkNotNull(deviceObject);

		deviceObject.setIsTimeSyncEnabled(deviceObjectDTO.getIsTimeSyncEnabled());
		deviceObject.setIsHexPassword(deviceObjectDTO.getIsHexPassword());
		deviceObject.setVersion(deviceObjectDTO.getVersion());


		if (deviceObjectDTO.getDeviceLoginInfo() != null && SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
		    deviceObject.setDevicePassword(deviceObjectDTO.getDeviceLoginInfo().getDevicePassword());
		    deviceObject.setDeviceLogin(deviceObjectDTO.getDeviceLoginInfo().getDeviceLogin());
        }

		deviceObjectRepository.save(deviceObject);

		DeviceObjectDataSource2 deviceObjectDataSource = deviceObject.getActiveDataSource();

		Optional.ofNullable(deviceObjectDTO).map(DeviceObjectDTO::getEditDataSourceInfo)
            .filter(i -> Objects.nonNull(i.getSubscrDataSourceAddr()))
            .filter(i -> Objects.nonNull(i.getSubscrDataSourceId()))
            .filter(i -> Objects.nonNull(deviceObjectDataSource))
            .filter(dto -> dto.getId() != null)
            .filter(dto -> deviceObjectDataSource != null &&
                dto.getId().equals(deviceObjectDataSource.getSubscrDataSource().getId()))
            .ifPresent(i -> {
                deviceObjectDataSource.setSubscrDataSourceAddr(i.getSubscrDataSourceAddr());
                deviceObjectDataSource2Repository.save(deviceObjectDataSource);
            });
//
//
//
//        if (deviceObjectDataSource != null && deviceObjectDTO.getEditDataSourceInfo() != null &&
//                deviceObjectDataSource.getId().equals(deviceObjectDTO.getEditDataSourceInfo().getId())) {
//            if (deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceAddr() != null)
//                deviceObjectDataSource.setSubscrDataSourceAddr(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceAddr());
//
//            deviceObjectDataSourceRepository.save(deviceObjectDataSource);
//        }


        DeviceObject result = selectDeviceObject(deviceObject.getId());

//        if (SecurityUtils.isCurrentUserInRole(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN)) {
//            result.shareDeviceLoginInfo();
//        }

        return result;
	}

    @Transactional( readOnly = true)
	public DeviceObjectDTO findDeviceObjectDTO(Long id) {
        DeviceObject deviceObject = selectDeviceObject(id);
        return deviceObjectMapper.toDto(deviceObject);
    }

//    public DeviceObjectDMO convert (DeviceObjectDTO deviceObjectDTO) {
//	    return modelMapper.map(deviceObjectDTO, DeviceObjectDMO.class);
//    }

	/**
	 *
	 * @param deviceObjectId
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObject(Long deviceObjectId) {
		checkNotNull(deviceObjectId);
		DeviceObject deviceObject = deviceObjectRepository.findOne(deviceObjectId);
		if (deviceObject == null) {
			throw new PersistenceException(String.format("DeviceObject (id=%d) is not found", deviceObjectId));
		}
		deviceObject.setDeleted(1);
		deviceObjectRepository.save(deviceObject);
	}

	/**
	 *
	 * @param deviceObjectId
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteDeviceObjectPermanent(Long deviceObjectId) {
		checkNotNull(deviceObjectId);
		DeviceObject deviceObject = deviceObjectRepository.findOne(deviceObjectId);
		if (deviceObject == null) {
			throw new PersistenceException(String.format("DeviceObject (id=%d) is not found", deviceObjectId));
		}

		Optional.ofNullable(deviceObject)
            .map(DeviceObject::getDeviceObjectDataSource)
            .ifPresent( i -> deviceObjectDataSource2Repository.delete(i));
//
//		if (deviceObject.getDeviceObjectDataSource() != null) {
//            deviceObjectDataSource2Repository.delete(deviceObject.getDeviceObjectDataSource());
//        }
//		deviceObject.getDeviceObjectDataSource().forEach(i -> {
//			deviceObjectDataSourceRepository.delete(i);
//		});

		deviceObjectRepository.delete(deviceObject);
	}

    /**
     *
     * @param subscriberId
     * @return
     */
	@Transactional( readOnly = true)
	public List<DeviceObject> selectDeviceObjectsBySubscriber(Long subscriberId) {
		//List<DeviceObject> result = objectAccessService.findAllContZPointDeviceObjects(subscriberId);
		List<DeviceObject> result = objectAccessService.findAllContObjectDeviceObjects(subscriberId);
		return result;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public DeviceObjectMetaVzlet deviceObjectMetaVzletInit(DeviceObject entity) {
		checkNotNull(entity);
		List<DeviceObjectMetaVzlet> metaVzletList = deviceObjectMetaVzletRepository
				.findByDeviceObjectId(entity.getId());
		DeviceObjectMetaVzlet result;
		if (metaVzletList.isEmpty()) {
			result = new DeviceObjectMetaVzlet();
			result.setDeviceObject(entity);
			result = updateDeviceObjectMetaVzlet(result);
		} else {
			result = metaVzletList.get(0);
		}

		return result;
	}

	@Transactional( readOnly = true)
	public List<DeviceObject> selectDeviceObjectByContZPoint(Long contZPointId) {
		return Lists.newArrayList(deviceObjectRepository.findAll());
	}

	/**
	 *
	 * @param ids
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<DeviceObject> selectDeviceObjectsByIds(Collection<Long> ids) {
		return ids.isEmpty() ? new ArrayList<>() : deviceObjectRepository.selectDeviceObjectsByIds(ids);
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional( readOnly = true)
	public V_DeviceObjectTimeOffset selectDeviceObjsetTimeOffset(Long deviceObjectId) {
		return deviceObjectId != null ? deviceObjectRepository.findOne(deviceObjectId).getTimeOffset() : null;
	}

    /**
     *
     * @param deviceObjectIds
     * @return
     */
	@Transactional( readOnly = true)
	public List<V_DeviceObjectTimeOffset> selectDeviceObjsetTimeOffset(List<Long> deviceObjectIds) {
		return deviceObjectIds.isEmpty() ? new ArrayList<>()
				: deviceObjectRepository.selectDeviceObjectsByIds(deviceObjectIds).stream().map(i -> i.getTimeOffset()).collect(Collectors.toList());
	}


    @Transactional( readOnly = true)
    public SubscrDataSourceDTO selectDeviceObjectSubscrDataSource(long deviceObjectId) {
        DeviceObject result = deviceObjectRepository.findOne(deviceObjectId);
        return Optional.ofNullable(result)
            .map(DeviceObject::getDeviceObjectDataSource)
            .map(DeviceObjectDataSource2::getSubscrDataSource)
            .map(i -> subscrDataSourceMapper.toDto(i)).orElse(null);
    }



}
