package ru.excbt.datafuse.nmk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.V_FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created by kovtonyk on 18.04.2017.
 */
@Component("portalUserDetailsService")
public class PortalUserDetailService implements org.springframework.security.core.userdetails.UserDetailsService  {

    @Autowired
    private V_FullUserInfoRepository v_fullUserInfoRepository;

    @Autowired
    private SubscrUserService subscrUserService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Supplier<UsernameNotFoundException> notFoundSupplier = () -> new UsernameNotFoundException(
            "User " + lowercaseLogin + " was not found in the " + "database");

        return v_fullUserInfoRepository.findOneByUserNameIgnoreCase(lowercaseLogin).map((user) -> {

            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            if (user.is_system()) {
                grantedAuths = AdminUtils.makeAuths(AuthoritiesConstants.adminAuthorities());
            } else {
                List<SubscrRole> roles = subscrUserService.selectSubscrRoles(user.getId());
                for (SubscrRole sr : roles) {
                    String roleName = sr.getRoleName();
                    grantedAuths.add(new SimpleGrantedAuthority(roleName));
                }
            }
            SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(user, "", grantedAuths);
            return subscriberUserDetails;
        }).orElseThrow(notFoundSupplier);

    }
}
