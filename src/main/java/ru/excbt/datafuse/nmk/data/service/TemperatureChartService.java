package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.repository.TemperatureChartItemRepository;
import ru.excbt.datafuse.nmk.data.repository.TemperatureChartRepository;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class TemperatureChartService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartService.class);

	@Autowired
	private TemperatureChartRepository temperatureChartRepository;

	@Autowired
	private TemperatureChartItemRepository temperatureChartItemRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private LocalPlaceService localPlaceService;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private FiasService fiasService;

	@Autowired
	private ContObjectFiasService contObjectFiasService;

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChart> selectTemperatureCharts() {
		return temperatureChartRepository.selectTemperatureCharts();
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChart> selectTemperatureChartsInfo() {
		List<TemperatureChart> result = temperatureChartRepository.selectTemperatureCharts();
		result.forEach(i -> {
			i.initLocalPlaceInfo();
			i.initRsoOrganizationInfo();
		});
		return result;
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChart> selectTemperatureChartsByContZPointId(Long contZPointId) {

		Long contObjectId = contZPointService.selectContObjectId(contZPointId);
		if (contObjectId == null) {
			throw new PersistenceException(String.format("ContZPoint (id=%d) is invalid", contZPointId));
		}

		return selectTemperatureChartsByContObjectId(contObjectId);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChart> selectTemperatureChartsByContObjectId(Long contObjectId) {

		checkNotNull(contObjectId);

		List<TemperatureChart> result = new ArrayList<>();

		ContObjectFias fias =  contObjectFiasService.findContObjectFias(contObjectId);

		if (fias == null || fias.getFiasUUID() == null) {
			return result;
		}

		UUID cityFiasUUID = fiasService.getCityUUID(fias.getFiasUUID());

		if (cityFiasUUID == null) {
			return result;
		}

		result = temperatureChartRepository.selectTemperatureChartsByFias(cityFiasUUID);
		result.forEach(i -> {
			i.initLocalPlaceInfo();
			i.initRsoOrganizationInfo();
		});

		return result;
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

		//Organization rsoOrg = organizationService.selectOrganization(entity.getRsoOrganizationId());

        Organization rsoOrg = organizationService.findOneOrganization(entity.getRsoOrganizationId())
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, entity.getRsoOrganizationId()));

		if (!Boolean.TRUE.equals(rsoOrg.getFlagRso())) {
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
		temperatureChartRepository.save(EntityActions.softDelete(entity));
	}

	/**
	 *
	 * @param temperatureChartId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TemperatureChartItem> selectTemperatureChartItems(Long temperatureChartId) {
		return ObjectFilters
				.deletedFilter(temperatureChartItemRepository.selectTemperatureChartItems(temperatureChartId));
	}

    /**
     *
     * @param temperatureChartItemId
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public TemperatureChartItem selectTemperatureChartItem(Long temperatureChartItemId) {
		return temperatureChartItemRepository.findOne(temperatureChartItemId);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public TemperatureChartItem saveTemperatureChartItem(TemperatureChartItem entity) {
		checkNotNull(entity);
		checkNotNull(entity.getTemperatureChartId());

		if (entity.getTemperatureChart() != null
				&& !entity.getTemperatureChartId().equals(entity.getTemperatureChart().getId())) {
			throw new IllegalArgumentException("TemperatureChartItem is invalid");
		}

		if (entity.getTemperatureChart() == null) {
			TemperatureChart chart = temperatureChartRepository.findOne(entity.getTemperatureChartId());
			if (chart == null) {
				throw new PersistenceException(
						String.format("TemperatureChart (id=%d) is not found", entity.getTemperatureChartId()));
			}

			entity.setTemperatureChart(chart);
		}

		return temperatureChartItemRepository.save(entity);
	}

	/**
	 *
	 * @param id
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteTemperatureChartItem(Long id) {
		TemperatureChartItem entity = temperatureChartItemRepository.findOne(id);
		if (entity == null) {
			throw new PersistenceException(String.format("TemperatureChartItem (id=%d) is not found", id));
		}
		temperatureChartItemRepository.save(EntityActions.softDelete(entity));
	}

}
