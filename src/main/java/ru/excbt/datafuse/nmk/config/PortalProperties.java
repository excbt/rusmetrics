package ru.excbt.datafuse.nmk.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 18.04.2017.
 */
@ConfigurationProperties(prefix = "portal")
public class PortalProperties {

    @Getter
    private final Security security = new Security();

    public static class Security {

        private final RememberMe rememberMe = new RememberMe();

        public RememberMe getRememberMe() {
            return rememberMe;
        }

        public static class RememberMe {

            @NotNull
            private String key;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }

}
