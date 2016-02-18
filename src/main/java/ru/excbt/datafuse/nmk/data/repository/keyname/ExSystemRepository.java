package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ExSystem;

/**
 * Repository для ExSystem
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.07.2015
 *
 */
public interface ExSystemRepository extends JpaRepository<ExSystem, String> {

}
