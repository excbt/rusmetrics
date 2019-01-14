package ru.excbt.datafuse.nmk.data.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	/**
	 *
	 * @param userName
	 * @return
	 */
	public List<V_FullUserInfo> findByUserName(String userName);

    Optional<V_FullUserInfo> findOneByUserNameIgnoreCase(String userName);

    @Query("select u.id from V_FullUserInfo u where lower(u.userName) = :username")
    Optional<Long> findOneIdByUserNameIgnoreCase(@Param("username") String userName);

	/**
	 *
	 * @param ids
	 * @return
	 */
	@Query("SELECT i FROM V_FullUserInfo i WHERE i.id in (:ids)")
	public List<V_FullUserInfo> selectFullUsersById(@Param("ids") Collection<Long> ids);

}
