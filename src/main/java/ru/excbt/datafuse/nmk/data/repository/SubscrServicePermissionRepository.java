package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;

/**
 * Repository для SubscrServicePermission
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.09.2015
 *
 */
public interface SubscrServicePermissionRepository extends JpaRepository<SubscrServicePermission, String> {

	@Query("SELECT sp FROM SubscrServicePermission sp WHERE sp.isCommon = TRUE")
	public List<SubscrServicePermission> selectCommonPermissions();
}
