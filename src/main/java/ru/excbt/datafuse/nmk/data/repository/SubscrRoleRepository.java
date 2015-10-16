package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;

public interface SubscrRoleRepository extends JpaRepository<SubscrRole, Long> {
	public List<SubscrRole> findByRoleName(String roleName);
}
