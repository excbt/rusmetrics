package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.repository.TemperatureChartRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class TemperatureChartService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartService.class);

	@Autowired
	private TemperatureChartRepository temperatureChartRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private LocalPlaceService localPlaceService;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChart> selectTemperatureChartAll() {
		return temperatureChartRepository.selectTemperatureCharts();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public TemperatureChart selectTemperatureChart(Long id) {
		return temperatureChartRepository.findOne(id);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public TemperatureChart saveTemperatureChart(TemperatureChart entity) {
		checkNotNull(entity);

		checkNotNull(entity.getRsoOrganizationId());
		checkNotNull(entity.getLocalPlaceId());

		Organization rsoOrg = organizationService.selectOrganization(entity.getRsoOrganizationId());
		if (rsoOrg == null || !Boolean.TRUE.equals(rsoOrg.getFlagRso())) {
			throw new IllegalArgumentException("Invalid rsoOrganizationId: " + entity.getRsoOrganizationId());
		}

		entity.setRsoOrganization(rsoOrg);

		LocalPlace localPlace = localPlaceService.findLocalPlace(entity.getLocalPlaceId());

		if (localPlace == null) {
			throw new IllegalArgumentException("Invalid localPlaceId: " + entity.getLocalPlaceId());
		}
		entity.setLocalPlace(localPlace);

		return temperatureChartRepository.save(entity);
	}

	/**
	 * 
	 * @param id
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTemperatureChart(Long id) {
		TemperatureChart entity = temperatureChartRepository.findOne(id);
		if (entity == null) {
			throw new PersistenceException(String.format("TemperatureChart (id=%d) is not found", id));
		}
		temperatureChartRepository.save(softDelete(entity));
	}

}
