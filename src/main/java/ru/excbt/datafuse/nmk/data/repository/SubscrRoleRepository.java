package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;

/**
 * Repository для SubscrRole
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
public interface SubscrRoleRepository extends JpaRepository<SubscrRole, Long> {
	public List<SubscrRole> findByRoleName(String roleName);
}
