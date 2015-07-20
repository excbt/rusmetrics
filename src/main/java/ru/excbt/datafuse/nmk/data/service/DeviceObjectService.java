package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class DeviceObjectService implements SecuredRoles {

	@Autowired
	private DeviceObjectRepository deviceObjectRepository;

	@Autowired
	private DeviceModelService DeviceModelService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public DeviceObject findOne(long id) {
		return deviceObjectRepository.findOne(id);
	}

	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public DeviceObject saveOne(DeviceObject deviceObject) {
		return deviceObjectRepository.save(deviceObject);
	}

	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public DeviceObject createPortalDeviceObject() {

		DeviceObject deviceObject = new DeviceObject();
		DeviceModel deviceModel = DeviceModelService.findPortalDeviceModel();
		checkNotNull(deviceModel, "DeviceModel of Portal is not found");

		deviceObject.setDeviceModel(deviceModel);
		deviceObject.setExSystem(ExSystemKey.PORTAL.getKeyname());
		return deviceObjectRepository.save(deviceObject);
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void deleteOne(Long deviceObjectId) {
		deviceObjectRepository.delete(deviceObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeviceObject> selectDeviceObjectsByContObjectId(Long contObjectId) {
		return deviceObjectRepository
				.selectDeviceObjectsByContObjectId(contObjectId);
	}

}
