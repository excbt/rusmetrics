package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.config.DefaultProfileUtil;
import ru.excbt.datafuse.nmk.config.PortalProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProfileInfoResource {

    private final Environment env;

    private final PortalProperties portalProperties;

    public ProfileInfoResource(Environment env, PortalProperties portalProperties) {
        this.env = env;
        this.portalProperties = portalProperties;
    }

    @GetMapping("/profile-info")
    public ProfileInfoVM getActiveProfiles() {
        String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        return new ProfileInfoVM(activeProfiles, getRibbonEnv(activeProfiles));
    }



    private String getRibbonEnv(String[] activeProfiles) {
        String[] displayOnActiveProfiles = portalProperties.getRibbon().getDisplayOnActiveProfiles();
        if (displayOnActiveProfiles == null) {
            return null;
        }
        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);
        if (!ribbonProfiles.isEmpty()) {
            return ribbonProfiles.get(0);
        }
        return null;
    }


    class ProfileInfoVM {

        private String[] activeProfiles;

        private String ribbonEnv;

        ProfileInfoVM(String[] activeProfiles, String ribbonEnv) {
            this.activeProfiles = activeProfiles;
            this.ribbonEnv = ribbonEnv;
        }

        public String[] getActiveProfiles() {
            return activeProfiles;
        }

        public String getRibbonEnv() {
            return ribbonEnv;
        }
    }

}
