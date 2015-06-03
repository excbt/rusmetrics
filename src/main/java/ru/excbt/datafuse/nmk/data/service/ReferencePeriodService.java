package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.repository.ReferencePeriodRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ReferencePeriodService implements SecuredRoles {

	private final static Pageable PAGE_LIMIT_1 = new PageRequest(0, 1);

	@Autowired
	private ReferencePeriodRepository referencePeriodRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReferencePeriod> findReferencePeriod(long subscriberId,
			long contZPointId) {
		return referencePeriodRepository.findBySubscriberIdAndContZPointId(
				subscriberId, contZPointId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ReferencePeriod> selectLastReferencePeriod(long subscriberId,
			long contZPointId) {
		return referencePeriodRepository.selectLastReferencePeriod(
				subscriberId, contZPointId, PAGE_LIMIT_1);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ReferencePeriod findOne(long referencePeriodId) {
		return referencePeriodRepository.findOne(referencePeriodId);
	}

	/**
	 * 
	 * @param referencePeriod
	 * @return
	 */
	public ReferencePeriod createOne(ReferencePeriod referencePeriod) {

		checkNotNull(referencePeriod);
		checkState(referencePeriod.isNew());
		checkNotNull(referencePeriod.getSubscriber());
		checkNotNull(referencePeriod.getContZPointId());

		ReferencePeriod result = referencePeriodRepository
				.save(referencePeriod);

		return result;
	}

	/**
	 * 
	 * @param referencePeriod
	 * @return
	 */
	public ReferencePeriod updateOne(ReferencePeriod referencePeriod) {

		checkNotNull(referencePeriod);
		checkState(!referencePeriod.isNew());
		checkNotNull(referencePeriod.getSubscriber());
		checkNotNull(referencePeriod.getContZPointId());

		ReferencePeriod result = referencePeriodRepository
				.save(referencePeriod);

		return result;
	}

	/**
	 * 
	 * @param referencePeriod
	 * @return
	 */
	public void deleteOne(ReferencePeriod referencePeriod) {

		checkNotNull(referencePeriod);
		checkState(!referencePeriod.isNew());

		deleteOne(referencePeriod.getId());

	}

	/**
	 * 
	 * @param referencePeriod
	 * @return
	 */
	public void deleteOne(long referencePeriodId) {
		if (referencePeriodRepository.exists(referencePeriodId)) {
			referencePeriodRepository.delete(referencePeriodId);
		} else {
			throw new PersistenceException(String.format(
					"ReperencePeriod with id=%d is not found",
					referencePeriodId));
		}
	}

}
