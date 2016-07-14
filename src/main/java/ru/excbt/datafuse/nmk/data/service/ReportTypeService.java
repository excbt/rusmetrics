package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTypeContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.vo.ReportTypeWithParamsVO;
import ru.excbt.datafuse.nmk.data.repository.ReportMetaParamCommonRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportMetaParamSpecialRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportTypeContServiceTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

/**
 * Сервис для работы с типами отчетов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.04.2015
 *
 */
@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
public class ReportTypeService {

	private static final Logger logger = LoggerFactory.getLogger(ReportTypeService.class);

	private static final Comparator<? super ReportType> COMP_REPORT_TYPE_ORDER = (t1, t2) -> {
		if (t1.getReportTypeOrder() != null && t2.getReportTypeOrder() != null) {
			return Integer.compare(t1.getReportTypeOrder(), t2.getReportTypeOrder());
		}
		return 0;
	};

	@Autowired
	private ReportTypeRepository reportTypeRepository;

	@Autowired
	private ReportMetaParamCommonRepository reportMetaParamCommonRepository;

	@Autowired
	private ReportMetaParamSpecialRepository reportMetaParamSpecialRepository;

	@Autowired
	private ReportTypeContServiceTypeRepository reportTypeContServiceTypeRepository;

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public ReportType findByKeyname(String keyname) {
		List<ReportType> resultList = reportTypeRepository.findByKeynameIgnoreCase(keyname);
		return resultList.size() == 1 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public ReportType selectReportType(String keyname) {
		return reportTypeRepository.findOne(keyname);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public ReportType findByKeyname(ReportTypeKey key) {
		List<ReportType> resultList = reportTypeRepository.findByKeynameIgnoreCase(key.name());
		return resultList.size() == 1 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	public List<ReportMetaParamSpecial> findReportMetaParamSpecialList(ReportTypeKey reportTypeKey) {
		List<ReportMetaParamSpecial> preResult = reportMetaParamSpecialRepository
				.findByReportTypeKeyname(reportTypeKey.getKeyname());
		return ObjectFilters.disabledFilter(preResult);
	}

	/**
	 * 
	 * @return
	 */
	public List<ReportType> findAllReportTypes() {
		return findAllReportTypes(false);
	}

	/**
	 * 
	 * @return
	 */
	public List<ReportType> findAllReportTypes(boolean devMode) {
		List<ReportType> preResult = reportTypeRepository.findAll();

		List<ReportType> result = preResult.stream().filter((i) -> !Boolean.TRUE.equals(i.getIsDevMode()) || devMode)
				.filter(ObjectFilters.NO_DISABLED_OBJECT_PREDICATE).filter(i -> Boolean.TRUE.equals(i.is_enabled()))
				.sorted(COMP_REPORT_TYPE_ORDER)
				.collect(Collectors.toList());

		return result;
	}

	/**
	 * 
	 * @param reportTypes
	 * @return
	 */
	public List<ReportTypeWithParamsVO> makeReportTypeParams(List<ReportType> reportTypes) {
		List<ReportTypeWithParamsVO> resultReportTypes = new ArrayList<>();

		for (ReportType rt : reportTypes) {
			ReportTypeWithParamsVO item = makeReportTypeParam(rt);
			resultReportTypes.add(item);
		}

		return resultReportTypes;
	}

	/**
	 * 
	 * @param reportType
	 * @return
	 */
	public ReportTypeWithParamsVO makeReportTypeParam(ReportType reportType) {
		List<ReportMetaParamCommon> paramCommons = reportMetaParamCommonRepository
				.selectByReportTypeKeyname(reportType.getKeyname());

		ReportMetaParamCommon paramCommon = paramCommons.size() == 1 ? paramCommons.get(0) : null;

		List<ReportMetaParamSpecial> paramSpecial = reportMetaParamSpecialRepository
				.findByReportTypeKeyname(reportType.getKeyname());

		List<ReportTypeContServiceType> serviceTypes = reportTypeContServiceTypeRepository
				.selectContServiceTypes(reportType.getKeyname());

		ReportTypeWithParamsVO item = new ReportTypeWithParamsVO(reportType, serviceTypes, paramCommon, paramSpecial);
		return item;

	}

}
