package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.EmailNotification;

public interface EmailNotificationRepository extends CrudRepository<EmailNotification, Long> {

	/**
	 * 
	 * @param fromSubscrUserId
	 * @return
	 */
	public List<EmailNotification> findByFromSubscrUserId(Long fromSubscrUserId);

	/**
	 * 
	 * @param fromSubscrUserId
	 * @return
	 */
	public List<EmailNotification> findByToSubscrUserId(Long toSubscrUserId);
}
