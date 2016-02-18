package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.TimeDetailType;

/**
 * Repository для TimeDetailType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.01.2015
 *
 */
public interface TimeDetailTypeRepository extends JpaRepository<TimeDetailType, String> {

}
