package ru.excbt.datafuse.nmk.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = { LocalSecurityConfig.class })
public class WebSecurityConfig {

}
