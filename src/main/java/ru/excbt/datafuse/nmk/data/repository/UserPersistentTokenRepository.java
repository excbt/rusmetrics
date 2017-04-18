package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.UserPersistentToken;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface UserPersistentTokenRepository extends JpaRepository<UserPersistentToken, String> {

    List<UserPersistentToken> findByUserId(Long userId);

    List<UserPersistentToken> findByTokenDateBefore(LocalDate localDate);

}
