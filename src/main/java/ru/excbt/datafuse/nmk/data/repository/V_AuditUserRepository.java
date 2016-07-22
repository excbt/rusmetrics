package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.V_AuditUser;

/**
 * Repository для AuditUser
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
public interface V_AuditUserRepository extends JpaRepository<V_AuditUser, Long> {

	public List<V_AuditUser> findByUserName(String userName);

}
