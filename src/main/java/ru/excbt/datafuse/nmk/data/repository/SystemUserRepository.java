package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SystemUser;

/**
 * Repository для SystemUser
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
public interface SystemUserRepository extends CrudRepository<SystemUser, Long> {

	public List<SystemUser> findByUserName(String userName);
}
