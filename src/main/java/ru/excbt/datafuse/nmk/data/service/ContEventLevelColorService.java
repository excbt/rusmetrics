package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventLevelColorRepository;

@Service
@Transactional(readOnly = true)
public class ContEventLevelColorService {

	private final ConcurrentHashMap<ContEventLevelColorKey, ContEventLevelColor> colorMap = new ConcurrentHashMap<>();

	@Autowired
	private ContEventLevelColorRepository contEventLevelColorRepository;

	/**
	 * 
	 * @param colorKey
	 * @return
	 */
	public ContEventLevelColor getEventColorCached(
			ContEventLevelColorKey colorKey, boolean resetCache) {

		ContEventLevelColor result = colorMap.get(colorKey);
		if (result != null && resetCache == false) {
			return result;
		}

		result = contEventLevelColorRepository.findOne(colorKey.getKeyname());
		checkNotNull(result);
		colorMap.put(colorKey, result);

		return result;
	}

	/**
	 * 
	 * @param colorKey
	 * @return
	 */
	public ContEventLevelColor getEventColorCached(
			ContEventLevelColorKey colorKey) {
		return getEventColorCached(colorKey, false);
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public ContEventLevelColor findOne(String keyname) {
		return contEventLevelColorRepository.findOne(keyname);
	}
}
