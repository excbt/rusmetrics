package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceModelHeatRadiator;
import ru.excbt.datafuse.nmk.data.model.QDeviceModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ImpulseCounterType;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelHeatRadiatorRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ImpulseCounterTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.QueryDSLUtil;
import ru.excbt.datafuse.nmk.service.dto.DeviceModelDTO;
import ru.excbt.datafuse.nmk.service.mapper.DeviceModelMapper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для работы с моделями прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2015
 *
 */
@Service
public class DeviceModelService implements SecuredRoles {

	private final DeviceModelRepository deviceModelRepository;


	private final ImpulseCounterTypeRepository impulseCounterTypeRepository;

	private final DeviceModelMapper deviceModelMapper;

	private final DeviceModelHeatRadiatorRepository deviceModelHeatRadiatorRepository;

	////////////
	public static final Comparator<DeviceModel> COMPARE_BY_NAME = (a, b) -> {
		if (a == b) {
			return 0;
		}
		if (a == null || a.getModelName() == null) {
			return -1;
		}
		if (b == null || b.getModelName() == null) {
			return 1;
		}

		return a.getModelName().compareTo(b.getModelName());
	};


	@Autowired
    public DeviceModelService(DeviceModelRepository deviceModelRepository,
                              ImpulseCounterTypeRepository impulseCounterTypeRepository,
                              DeviceModelMapper deviceModelMapper,
                              DeviceModelHeatRadiatorRepository deviceModelHeatRadiatorRepository) {
	    this.deviceModelRepository = deviceModelRepository;
	    this.impulseCounterTypeRepository = impulseCounterTypeRepository;
	    this.deviceModelMapper = deviceModelMapper;
	    this.deviceModelHeatRadiatorRepository = deviceModelHeatRadiatorRepository;
    }

    /**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional
	public DeviceModel save(DeviceModel entity) {
		return deviceModelRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional
	public void delete(DeviceModel entity) {
		deviceModelRepository.delete(entity);
	}

	/**
	 *
	 * @param id
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional
	public void delete(Long id) {
		deviceModelRepository.delete(id);
	}

	/**
	 *
	 * @param exSystem
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceModel> findDeviceModelsByExSystem(String exSystem) {

		List<DeviceModel> result = deviceModelRepository.findByExSystem(exSystem);

		return ObjectFilters.deletedFilter(result);
	}

	/**
	 *
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceModel findPortalDeviceModel() {

		List<DeviceModel> pre = findDeviceModelsByExSystem(ExSystemKey.PORTAL.getKeyname());

		Optional<DeviceModel> result = pre.stream().sorted((m1, m2) -> Long.compare(m1.getId(), m2.getId()))
				.findFirst();

		return result.isPresent() ? result.get() : null;
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceModel findDeviceModel(Long id) {
		return deviceModelRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public DeviceModelDTO findDeviceModelDTO(Long id) {

	    DeviceModelDTO deviceModelDTO = deviceModelMapper.toDto(deviceModelRepository.findOne(id));

	    if (deviceModelDTO != null) {
	        deviceModelHeatRadiatorRepository.findByDeviceModel(deviceModelDTO.getId())
                .forEach((i) -> deviceModelDTO.getHeatRadiatorKcs().put(i.getDeviceModelHeatRadiatorPK().getHeatRadiatorType().getId(), i.getKc()));
        }

		return deviceModelDTO;
	}

	/**
	 *
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceModel> findDeviceModelAll() {
		List<DeviceModel> preResult = Lists.newArrayList(deviceModelRepository.findAll());
		return ObjectFilters.deletedFilter(preResult);
	}

	@Transactional(readOnly = true)
	public List<DeviceModelDTO> findDeviceModels() {
		List<DeviceModel> deviceModels = Lists.newArrayList(deviceModelRepository.findAll());

        deviceModels.sort(DeviceModelService.COMPARE_BY_NAME);

        List<DeviceModelDTO> resultList = deviceModels.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map((i) -> deviceModelMapper.toDto(i)).collect(Collectors.toList());


        List<DeviceModelHeatRadiator> heatRadiatorsAll = deviceModelHeatRadiatorRepository.findAll();

        resultList.forEach((i) -> {

            heatRadiatorsAll.stream()
                .filter((r) -> r.getDeviceModelHeatRadiatorPK().getDeviceModel().getId().equals(i.getId()))
                .forEach((r) ->
                    i.getHeatRadiatorKcs().put(r.getDeviceModelHeatRadiatorPK().getHeatRadiatorType().getId(), r.getKc())
                );
        });

        return resultList;
	}


    private static BooleanExpression searchCondition(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        QDeviceModel qDeviceModel = QDeviceModel.deviceModel;
        Function<String, BooleanExpression> exprBuilder =
            builderString -> qDeviceModel.modelName.toLowerCase().like(QueryDSLUtil.lowerCaseLikeStr.apply(builderString));

        return QueryDSLUtil.buildSearchCondition(s, exprBuilder);
    }


    @Transactional(readOnly = true)
    public Page<DeviceModelDTO> findDeviceModels(String searchString, Pageable pageable) {

        QDeviceModel qDeviceModel = QDeviceModel.deviceModel;

        WhereClauseBuilder whereBuilder = new WhereClauseBuilder()
            .and(qDeviceModel.deleted.eq(0));

        Optional.ofNullable(searchString).filter(i -> !i.isEmpty())
            .ifPresent(s -> whereBuilder.and(searchCondition(s)));

        Page<DeviceModel> rawData = deviceModelRepository.findAll(whereBuilder, pageable);

        List<DeviceModelHeatRadiator> heatRadiatorsAll = deviceModelHeatRadiatorRepository.findAll();

        Page<DeviceModelDTO> dtoData = rawData.map(deviceModelMapper::toDto);
        dtoData.getContent().forEach((i) -> heatRadiatorsAll.stream()
            .filter((r) -> r.getDeviceModelHeatRadiatorPK().getDeviceModel().getId().equals(i.getId()))
            .forEach((r) ->
                i.getHeatRadiatorKcs().put(r.getDeviceModelHeatRadiatorPK().getHeatRadiatorType().getId(), r.getKc())
            ));


	    return dtoData;
    }

//	/**
//	 *
//	 * @return
//	 */
//	@Transactional( readOnly = true)
//	public List<DeviceModelDataType> findDeviceModelTypes() {
//		return deviceModelDataTypeRepository.selectAll();
//	}

	/**
	 *
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ImpulseCounterType> findImpulseCounterTypes() {
		return impulseCounterTypeRepository.selectAllOrdered();
	}

}
