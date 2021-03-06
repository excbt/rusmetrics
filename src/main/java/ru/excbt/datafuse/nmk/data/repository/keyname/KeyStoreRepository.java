package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.KeyStore;

/**
 * Repository для KeyStore
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.07.2015
 *
 */
public interface KeyStoreRepository extends JpaRepository<KeyStore, String> {

}
