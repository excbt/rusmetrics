package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.dto.UserDTO;
import ru.excbt.datafuse.nmk.web.rest.errors.InternalServerErrorException;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final PortalUserIdsService portalUserIdsService;

    @Autowired
    public AccountResource(PortalUserIdsService portalUserIdsService) {
        this.portalUserIdsService = portalUserIdsService;
    }

    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    @GetMapping("/account")
    @Timed
    public UserDTO getAccount() {
        return portalUserIdsService.getSubscrUserWithAuthorities().map(UserDTO::new)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
    }

}
