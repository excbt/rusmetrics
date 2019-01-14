package ru.excbt.datafuse.nmk.web.api.support;

import java.net.URI;

/**
 * Интерфейс для работы с action при POST
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
public interface ApiActionLocation extends ApiAction {

	URI getLocation();
}
