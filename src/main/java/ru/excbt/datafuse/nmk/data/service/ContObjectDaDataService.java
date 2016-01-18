package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectDaData;
import ru.excbt.datafuse.nmk.data.repository.ContObjectDaDataRepository;

@Service
public class ContObjectDaDataService {

	private static final Logger logger = LoggerFactory.getLogger(ContObjectDaDataService.class);

	@Autowired
	private ContObjectDaDataRepository contObjectDaDataRepository;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContObjectDaData findByContObjectId(Long contObjectId) {
		List<ContObjectDaData> preResult = contObjectDaDataRepository.findByContObjectId(contObjectId);
		if (preResult.size() == 0) {
			return null;
		}
		return preResult.get(0);
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ContObjectDaData getContObjectDaData(ContObject contObject) {
		ContObjectDaData result = null;
		if (contObject.isNew()) {
			result = new ContObjectDaData();
			result.setContObject(contObject);
		} else {
			result = findByContObjectId(contObject.getId());
			if (result == null) {
				result = new ContObjectDaData();
				result.setContObject(contObject);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public ContObjectDaData processContObjectDaData(ContObjectDaData contObjectDaData) {
		checkNotNull(contObjectDaData);
		parseSraw(contObjectDaData);
		return contObjectDaDataRepository.save(contObjectDaData);
	}

	/**
	 * 
	 * @param contObjectDaData
	 */
	private void parseSraw(ContObjectDaData contObjectDaData) {
		if (contObjectDaData.getSraw() == null) {
			return;
		}

		String raw = contObjectDaData.getSraw();

		contObjectDaData.setSvalue(safeJsonRead(raw, "$.value"));
		contObjectDaData.setDataRegionTypeFull(safeJsonRead(raw, "$.data.region_type_full"));
		contObjectDaData.setDataRegion(safeJsonRead(raw, "$.data.region"));
		contObjectDaData.setDataAreaTypeFull(safeJsonRead(raw, "$.data.area_type_full"));
		contObjectDaData.setDataArea(safeJsonRead(raw, "$.data.area"));
		contObjectDaData.setDataCityTypeFull(safeJsonRead(raw, "$.data.city_type_full"));
		contObjectDaData.setDataCity(safeJsonRead(raw, "$.data.city"));
		contObjectDaData.setDataSettlementTypeFull(safeJsonRead(raw, "$.data.settlement_type_full"));
		contObjectDaData.setDataSettlement(safeJsonRead(raw, "$.data.settlement"));
		contObjectDaData.setDataCityDistrict(safeJsonRead(raw, "$.data.city_district"));
		contObjectDaData.setDataStreetTypeFull(safeJsonRead(raw, "$.data.street_type_full"));
		contObjectDaData.setDataStreet(safeJsonRead(raw, "$.data.street"));
		contObjectDaData.setDataHouseTypeFull(safeJsonRead(raw, "$.data.house_type_full"));
		contObjectDaData.setDataHouse(safeJsonRead(raw, "$.data.house"));
		contObjectDaData.setDataBlockTypeFull(safeJsonRead(raw, "$.data.block_type_full"));
		contObjectDaData.setDataBlock(safeJsonRead(raw, "$.data.block"));
		contObjectDaData.setDataFlatTypeFull(safeJsonRead(raw, "$.data.flat_type_full"));
		contObjectDaData.setDataFlat(safeJsonRead(raw, "$.data.flat"));
		contObjectDaData.setDataFiasId(UUID.fromString(safeJsonRead(raw, "$.data.fias_id")));

		String lat = safeJsonRead(raw, "$.data.geo_lat");
		String lon = safeJsonRead(raw, "$.data.geo_lon");
		if (lat != null || lon != null) {
			contObjectDaData.setDataGeoLat(new BigDecimal(lat));
			contObjectDaData.setDataGeoLon(new BigDecimal(lon));
		}

	}

	/**
	 * 
	 * @param json
	 * @param jsonPath
	 * @return
	 */
	private <T> T safeJsonRead(String json, String jsonPath) {

		T result = null;
		try {
			result = JsonPath.read(json, jsonPath);
		} catch (Exception e) {
			logger.error("Can't parse json by jsonPath:" + jsonPath, e);
		}

		return result;
	}

}
