package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.repository.ReportMetaParamSpecialRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportTypeRepository;

@Service
@Transactional(readOnly = true)
public class ReportTypeService {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportTypeService.class);

	@Autowired
	private ReportTypeRepository reportTypeRepository;

	@Autowired
	private ReportMetaParamSpecialRepository reportMetaParamSpecialRepository;

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public ReportType findByKeyname(String keyname) {
		List<ReportType> resultList = reportTypeRepository
				.findByKeynameIgnoreCase(keyname);
		return resultList.size() == 1 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public ReportType findByKeyname(ReportTypeKey key) {
		List<ReportType> resultList = reportTypeRepository
				.findByKeynameIgnoreCase(key.name());
		return resultList.size() == 1 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @return
	 */
	public List<ReportMetaParamSpecial> findReportMetaParamSpecialList(
			ReportTypeKey reportTypeKey) {
		return reportMetaParamSpecialRepository
				.findByReportTypeKey(reportTypeKey);
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

		List<ReportType> result = preResult.stream().sorted((t1, t2) -> {
			if (t1.getCaption() != null && t2.getCaption() != null) {
				return t1.getCaption().compareTo(t2.getCaption());
			}
			return 0;
		}).filter((i) -> !Boolean.TRUE.equals(i.getIsDevMode()) || devMode)
				.collect(Collectors.toList());

		return result;
	}

}
