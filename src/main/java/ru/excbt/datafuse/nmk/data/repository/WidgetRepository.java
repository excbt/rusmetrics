/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.Widget;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 * 
 */
public interface WidgetRepository extends JpaRepository<Widget, Long> {

}
