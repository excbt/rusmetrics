package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;

public interface FullUserInfoRepository extends JpaRepository<FullUserInfo, Long> {

	public List<FullUserInfo> findByUserName (String userName);
}
