package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.VzletSystem;

/**
 * Repository для VzletSystem
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 21.07.2015
 *
 */
public interface VzletSystemRepository extends JpaRepository<VzletSystem, Long> {

}
