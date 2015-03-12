package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SystemUser;

public interface SystemUserRepository extends CrudRepository<SystemUser, Long> {

}
