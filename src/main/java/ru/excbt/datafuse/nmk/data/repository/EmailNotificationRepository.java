package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.EmailNotification;

public interface EmailNotificationRepository extends CrudRepository<EmailNotification, Long> {

}
