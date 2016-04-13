package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrObjectTreeContObjectService implements SecuredRoles {

	@Autowired
	private SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectContObjects(Long subscrObjectTreeId) {
		return subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTreeId);
	}

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectContObjectIds(Long subscrObjectTreeId) {
		return subscrObjectTreeContObjectRepository.selectContObjectIds(subscrObjectTreeId);
	}

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @param contObjectIds
	 */
	@Secured({ ROLE_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void saveContObjects(Long subscrObjectTreeId, List<Long> contObjectIds) {

		List<SubscrObjectTreeContObject> contObjects = subscrObjectTreeContObjectRepository
				.selectSubscrObjectTreeContObject(subscrObjectTreeId);

		List<SubscrObjectTreeContObject> saveContObjects = new ArrayList<>();
		List<SubscrObjectTreeContObject> deleteContObjects = new ArrayList<>();

		for (SubscrObjectTreeContObject co : contObjects) {
			if (contObjectIds.contains(co.getContObjectId())) {
				saveContObjects.add(co);
			} else {
				deleteContObjects.add(co);
			}
		}

		List<Long> savedIds = saveContObjects.stream().map(i -> i.getContObjectId()).collect(Collectors.toList());

		for (Long id : contObjectIds) {
			if (!savedIds.contains(id)) {
				SubscrObjectTreeContObject co = new SubscrObjectTreeContObject();
				co.setSubscrObjectTreeId(subscrObjectTreeId);
				co.setContObjectId(id);
				saveContObjects.add(co);
			}
		}

		subscrObjectTreeContObjectRepository.save(saveContObjects);
		subscrObjectTreeContObjectRepository.delete(deleteContObjects);
	}

}
