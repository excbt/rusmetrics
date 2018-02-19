package ru.excbt.datafuse.nmk.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.security.PortalUserAuthenticationProvider;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
//@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Profile(value = "!SAML")
public class LocalSecurityConfig extends WebSecurityConfigurerAdapter {


	private final PortalUserAuthenticationProvider userAuthenticationProvider;

    private final RememberMeServices rememberMeServices;

    private final PortalProperties portalProperties;

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    @Autowired
    public LocalSecurityConfig(PortalUserAuthenticationProvider userAuthenticationProvider, RememberMeServices rememberMeServices, PortalProperties portalProperties, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        //this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.rememberMeServices = rememberMeServices;
        this.portalProperties = portalProperties;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @Autowired
	public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
	}


    @Bean
    public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    public AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler() {
        return new AjaxLogoutSuccessHandler();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            //.antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");


        web.ignoring()
            //.antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/v2/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**")
            .antMatchers("/h2-console/**");
    }



	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// выключаем защиту от CSRF атак
		http
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
            .addFilterBefore(corsFilter, CsrfFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            //.accessDeniedHandler(problemSupport)
        .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices)
            .rememberMeParameter("remember-me")
            .key(portalProperties.getSecurity().getRememberMe().getKey())
       .and()

            .formLogin()
            // указываем страницу с формой логина
            .loginProcessingUrl("/api/authentication")
            .loginPage("/auth/login")
            .successHandler(ajaxAuthenticationSuccessHandler())
            .failureHandler(ajaxAuthenticationFailureHandler())
            //.failureHandler(myajaxAuthenticationFailureHandler)
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll()

       .and()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(ajaxLogoutSuccessHandler())
            .permitAll()
       .and()
            .headers()
            .frameOptions()
            .disable()
       .and()
            .authorizeRequests()

            .antMatchers("/").permitAll()
            .antMatchers("/WEB-INF/**").denyAll()
            .antMatchers("/app/**").permitAll()
            .antMatchers("/api/benchmark/**").permitAll()
            .antMatchers("/v2/**").permitAll()
            //.antMatchers("/login*").permitAll()
            //.antMatchers("/localLogin").permitAll()
            .antMatchers("/api/appStatus/**").permitAll()
            .antMatchers("/api/securityCheck/**").permitAll()
            .antMatchers("/api/www-metrics").permitAll()
            .antMatchers("/api/**").access(RolesAccess.API_SUBSR_ACCESS)//.authenticated()
            .antMatchers("/api/rma/**").access(RolesAccess.API_RMA_ACCESS)
            .antMatchers("/resources/**").permitAll()
            .antMatchers("/bower_components/**").permitAll()
            .antMatchers("/vendor_components/**").permitAll()

            .antMatchers("/auth/login").permitAll()
            .antMatchers("/auth/localLogin").permitAll()
            .antMatchers("/auth/logout").permitAll()

            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/profile-info").permitAll();


	}

	/**
	 *
	 * @return
	 */
	@Bean(name = "sessionRegistry")
	public SessionRegistry getSessionRegistry() {
		return new SessionRegistryImpl();
	}

//	/**
//	 *
//	 * @return
//	 */
//	@Bean
//	public MyAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
//		return new MyAuthenticationSuccessHandler();
//	}

}
