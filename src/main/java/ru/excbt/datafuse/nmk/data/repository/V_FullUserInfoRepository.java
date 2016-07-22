package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;

/**
 * Repository для FullUserInfo
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface V_FullUserInfoRepository extends JpaRepository<V_FullUserInfo, Long> {

	public List<V_FullUserInfo> findByUserName(String userName);
}
