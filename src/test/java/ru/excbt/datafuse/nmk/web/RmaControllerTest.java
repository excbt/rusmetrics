package ru.excbt.datafuse.nmk.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

import java.util.Collection;

@WithMockUser(username = "rma-ex1", password = "12345",
		roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
				"RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", })
public class RmaControllerTest extends AnyControllerTest implements TestExcbtRmaIds {

    private static final Logger log = LoggerFactory.getLogger(RmaControllerTest.class);


	/*

	 */
	@Override
	public long getSubscriberId() {
		return EXCBT_RMA_SUBSCRIBER_ID;
	}

	/*

	 */
	@Override
	public long getSubscrUserId() {
		return EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

    /**
     * Show current roles for user
     */
    public void showAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        log.info("User Authoriities");
        authorities.stream().forEach(i -> {
            log.info(i.getAuthority());
        });

    }
}
