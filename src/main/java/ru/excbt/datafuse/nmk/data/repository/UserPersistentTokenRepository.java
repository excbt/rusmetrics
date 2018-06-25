package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.UserPersistentToken;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface UserPersistentTokenRepository extends JpaRepository<UserPersistentToken, String> {

    List<UserPersistentToken> findByUserId(Long userId);

    List<UserPersistentToken> findByTokenDateBefore(LocalDate localDate);

//    @Modifying
//    @Transactional
//    @Query("DELETE from UserPersistentToken p where p.series=:series")
//    void deleteTokenBySeries(@Param("series") String series);

}
