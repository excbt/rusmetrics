package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;

public interface SubscrVCookieRepository extends CrudRepository<SubscrVCookie, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT vc FROM SubscrVCookie vc WHERE vc.subscriberId = :subscriberId and vc.subscrUserId is null")
	public List<SubscrVCookie> selectSubscrVCookie(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrUserId
	 * @return
	 */
	@Query("SELECT vc FROM SubscrVCookie vc WHERE vc.subscriberId = :subscriberId and vc.subscrUserId = :subscrUserId")
	public List<SubscrVCookie> selectSubscrVCookie(@Param("subscriberId") Long subscriberId,
			@Param("subscrUserId") Long subscrUserId);

}
