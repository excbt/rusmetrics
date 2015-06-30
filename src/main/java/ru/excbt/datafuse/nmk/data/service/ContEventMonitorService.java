package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorRepository;

@Service
@Transactional
public class ContEventMonitorService {

	public final static Comparator<ContEventMonitor> CMP_BY_COLOR_RANK = (e1,
			e2) -> Integer.compare(e1.getContEventLevelColor() == null ? -1
			: e1.getContEventLevelColor().getColorRank(), e2
			.getContEventLevelColor() == null ? -1 : e2
			.getContEventLevelColor().getColorRank());

	@Autowired
	private ContEventMonitorRepository contEventMonitorRepository;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContEventMonitor> findContEventMonitor(Long contObjectId) {
		checkNotNull(contObjectId);
		List<ContEventMonitor> contEventMonitor = contEventMonitorRepository
				.findByContObjectId(contObjectId);
		return contEventMonitor;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContEventLevelColorKey findContEventMonitorColor(Long contObjectId) {
		checkNotNull(contObjectId);
		List<ContEventMonitor> contEventMonitor = contEventMonitorRepository
				.findByContObjectId(contObjectId);

		if (contEventMonitor.isEmpty()) {
			return null;
		}

		Optional<ContEventMonitor> sorted = contEventMonitor.stream()
				.sorted(CMP_BY_COLOR_RANK).findFirst();

		if (!sorted.isPresent()) {
			return null;
		}

		ContEventLevelColorKey result = ContEventLevelColorKey.findByKey(sorted
				.get().getContEventLevelColor().getKeyname());
		return result;
	}
}
