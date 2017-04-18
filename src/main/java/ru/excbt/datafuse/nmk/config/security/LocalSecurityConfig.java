package ru.excbt.datafuse.nmk.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.RememberMeServices;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.security.UserAuthenticationProvider;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Profile(value = "!SAML")
public class LocalSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	@Autowired
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private PortalProperties portalProperties;

	@Autowired
	public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// выключаем защиту от CSRF атак
		http.csrf().disable()
				// указываем правила запросов
				// по которым будет определятся доступ к ресурсам и остальным
				// данным
				.authorizeRequests().antMatchers("/").permitAll().antMatchers("/WEB-INF/**").denyAll()
				.antMatchers("/app/**").authenticated().antMatchers("/api/benchmark/**").permitAll()
				.antMatchers("/api/appStatus/**").permitAll().antMatchers("/api/securityCheck/**").permitAll()
                .antMatchers("/api/www-metrics").permitAll()
				.antMatchers("/api/**").access(RolesAccess.API_SUBSR_ACCESS)//.authenticated()
				.antMatchers("/api/rma/**").access(RolesAccess.API_RMA_ACCESS).antMatchers("/resources/**").permitAll()

				.antMatchers("/bower_components/**").permitAll().antMatchers("/vendor_components/**").permitAll().and();

		http.sessionManagement().maximumSessions(5).sessionRegistry(getSessionRegistry()).expiredUrl("/login");

		http.rememberMe()
            .rememberMeServices(rememberMeServices)
            .rememberMeParameter("remember-me")
            .key(portalProperties.getSecurity().getRememberMe().getKey());

		http.formLogin()
				// указываем страницу с формой логина
				.loginPage("/login")
				// указываем action с формы логина
				.loginProcessingUrl("/j_spring_security_check").defaultSuccessUrl("/app/", true)
				// указываем URL при неудачном логине
				.failureUrl("/login?error")
				// Указываем параметры логина и пароля с формы логина
				.usernameParameter("j_username").passwordParameter("j_password")
				.successHandler(authenticationSuccessHandler)
				// даем доступ к форме логина всем
				.permitAll();

		http.logout()
				// разрешаем делать логаут всем
				.permitAll()
				// указываем URL логаута
				.logoutUrl("/logout")
				// указываем URL при удачном логауте
				.logoutSuccessUrl("/login?logout")
				// делаем не валидной текущую сессию
				.invalidateHttpSession(true);

	}

	/**
	 *
	 * @return
	 */
	@Bean(name = "sessionRegistry")
	public SessionRegistry getSessionRegistry() {
		return new SessionRegistryImpl();
	}

	/**
	 *
	 * @return
	 */
	@Bean
	public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

}
