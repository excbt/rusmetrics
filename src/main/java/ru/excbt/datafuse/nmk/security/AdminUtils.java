package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Утилиты для работы с правами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 05.08.2015
 *
 */
public class AdminUtils {

	private AdminUtils() {

	}

	public static List<GrantedAuthority> makeAuths(Collection<String> roles) {
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        roles.forEach(r -> grantedAuths.add(new SimpleGrantedAuthority(r)));
        return grantedAuths;
    }


}
