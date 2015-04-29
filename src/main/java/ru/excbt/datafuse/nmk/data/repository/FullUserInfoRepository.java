package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;

public interface FullUserInfoRepository extends CrudRepository<FullUserInfo, Long> {

	public List<FullUserInfo> findByUserName (String userName);
}
