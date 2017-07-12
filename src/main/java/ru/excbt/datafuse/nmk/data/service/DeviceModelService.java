package ru.excbt.datafuse.nmk.data.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceModelHeatRadiator;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceModelDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ImpulseCounterType;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelHeatRadiatorRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceModelRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ImpulseCounterTypeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.DeviceModelMapper;

/**
 * Сервис для работы с моделями прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2015
 *
 */
@Service
public class DeviceModelService extends AbstractService implements SecuredRoles {

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
	@Transactional(value = TxConst.TX_DEFAULT)
	public DeviceModel save(DeviceModel entity) {
		return deviceModelRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void delete(DeviceModel entity) {
		deviceModelRepository.delete(entity);
	}

	/**
	 *
	 * @param id
	 */
	@Secured({ ROLE_DEVICE_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void delete(Long id) {
		deviceModelRepository.delete(id);
	}

	/**
	 *
	 * @param exSystem
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceModel> findDeviceModelsByExSystem(String exSystem) {

		List<DeviceModel> result = deviceModelRepository.findByExSystem(exSystem);

		return ObjectFilters.deletedFilter(result);
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceModel findDeviceModel(Long id) {
		return deviceModelRepository.findOne(id);
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public DeviceModelDTO findDeviceModelDTO(Long id) {

	    DeviceModelDTO deviceModelDTO = deviceModelMapper.deviceModelToDto(deviceModelRepository.findOne(id));

	    if (deviceModelDTO != null) {
	        deviceModelHeatRadiatorRepository.findByDeviceModel(deviceModelDTO.getId())
                .forEach((i) -> deviceModelDTO.getHeatRadiatorKcs().put(i.getDeviceModelHeatRadiatorId().getHeatRadiatorType().getId(), i.getKc()));
        }

		return deviceModelDTO;
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceModel> findDeviceModelAll() {
		List<DeviceModel> preResult = Lists.newArrayList(deviceModelRepository.findAll());
		return ObjectFilters.deletedFilter(preResult);
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceModelDTO> findDeviceModelDTOs() {
		List<DeviceModel> deviceModels = Lists.newArrayList(deviceModelRepository.findAll());

        deviceModels.sort(DeviceModelService.COMPARE_BY_NAME);

        List<DeviceModelDTO> resultList = deviceModels.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map((i) -> deviceModelMapper.deviceModelToDto(i)).collect(Collectors.toList());


        List<DeviceModelHeatRadiator> heatRadiatorsAll = deviceModelHeatRadiatorRepository.findAll();

        resultList.forEach((i) -> {

            heatRadiatorsAll.stream()
                .filter((r) -> r.getDeviceModelHeatRadiatorId().getDeviceModel().getId().equals(i.getId()))
                .forEach((r) ->
                    i.getHeatRadiatorKcs().put(r.getDeviceModelHeatRadiatorId().getHeatRadiatorType().getId(), r.getKc())
                );
        });

        return resultList;
	}

//	/**
//	 *
//	 * @return
//	 */
//	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
//	public List<DeviceModelDataType> findDeviceModelTypes() {
//		return deviceModelDataTypeRepository.selectAll();
//	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ImpulseCounterType> findImpulseCounterTypes() {
		return impulseCounterTypeRepository.selectAllOrdered();
	}

}
