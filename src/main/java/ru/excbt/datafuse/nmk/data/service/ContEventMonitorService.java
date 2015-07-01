package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.domain.KeynameObject;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorRepository;

@Service
@Transactional(readOnly = true)
public class ContEventMonitorService {

	public final static Comparator<ContEventMonitor> CMP_BY_COLOR_RANK = (e1,
			e2) -> Integer.compare(e1.getContEventLevelColor() == null ? -1
			: e1.getContEventLevelColor().getColorRank(), e2
			.getContEventLevelColor() == null ? -1 : e2
			.getContEventLevelColor().getColorRank());

	public final static Comparator<ContEventMonitor> CMP_BY_EVENT_TIME = (e1,
			e2) -> e1.getContEventTime().compareTo(e2.getContEventTime());

	@Autowired
	private ContEventMonitorRepository contEventMonitorRepository;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEventMonitor> findByContObject(Long contObjectId) {
		checkNotNull(contObjectId);

		List<ContEventMonitor> contEventMonitor = contEventMonitorRepository
				.findByContObjectId(contObjectId);

		List<ContEventMonitor> result = contEventMonitor.stream()
				.sorted(CMP_BY_EVENT_TIME).collect(Collectors.toList());

		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public ContEventLevelColor getColorByContObject(Long contObjectId) {
		checkNotNull(contObjectId);
		List<ContEventMonitor> contEventMonitor = contEventMonitorRepository
				.findByContObjectId(contObjectId);
		return sortWorseColor(contEventMonitor);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public ContEventLevelColorKey getColorKeyByContObject(Long contObjectId) {
		return getColorKey(getColorByContObject(contObjectId));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public List<ContEventMonitor> selectBySubscriberId(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContEventMonitor> result = contEventMonitorRepository
				.selectBySubscriberId(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public ContEventLevelColorKey getColorKeyBySubscriberId(Long subscriberId) {
		return getColorKey(getColorBySubscriberId(subscriberId));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public ContEventLevelColor getColorBySubscriberId(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContEventMonitor> contEventMonitor = contEventMonitorRepository
				.selectBySubscriberId(subscriberId);

		return sortWorseColor(contEventMonitor);
	}

	/**
	 * 
	 * @param contEventMonitor
	 * @return
	 */
	private ContEventLevelColor sortWorseColor(
			List<ContEventMonitor> contEventMonitor) {

		checkNotNull(contEventMonitor);

		if (contEventMonitor.isEmpty()) {
			return null;
		}

		Optional<ContEventMonitor> sorted = contEventMonitor.stream()
				.sorted(CMP_BY_COLOR_RANK).findFirst();

		if (!sorted.isPresent()) {
			return null;
		}

		return sorted.get().getContEventLevelColor();
	}

	/**
	 * 
	 * @param keynameObject
	 * @return
	 */
	private ContEventLevelColorKey getColorKey(KeynameObject keynameObject) {
		if (keynameObject == null) {
			return null;
		}
		ContEventLevelColorKey result = ContEventLevelColorKey
				.findByKeyname(keynameObject);
		return result;

	}

}
