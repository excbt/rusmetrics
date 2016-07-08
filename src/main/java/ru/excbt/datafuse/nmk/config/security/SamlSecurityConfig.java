package ru.excbt.datafuse.nmk.config.security;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import ru.excbt.datafuse.nmk.security.SAMLSubscriberUserDetailsService;
import ru.excbt.datafuse.nmk.security.UserAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@PropertySource(value = "classpath:META-INF/saml-idp.properties")
@ComponentScan(basePackages = { "org.springframework.security.saml" })
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SamlSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SamlSecurityConfig.class);

	@Value("${saml.entityId}")
	private String samlEntityId;

	// @Value("${saml.metadataProvider}")
	// private String samlMetadataProvider;

	@Value("${saml.metadataProviderFile}")
	private String samlMetadataProviderFile;

	@Value("${saml.forceLocalLogin}")
	private boolean forceLocalLogin;

	@Autowired
	private SAMLSubscriberUserDetailsService samlSubscriberUserDetailsService;

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	// XML parser pool needed for OpenSAML parsing
	@Bean(initMethod = "initialize")
	public StaticBasicParserPool parserPool() {
		StaticBasicParserPool result = new StaticBasicParserPool();
		Map<String, Boolean> features = new HashMap<>();
		features.put("http://apache.org/xml/features/dom/defer-node-expansion", false);
		result.setBuilderFeatures(features);
		return result;
	}

	@Bean(name = "parserPoolHolder")
	public ParserPoolHolder parserPoolHolder() {
		return new ParserPoolHolder();
	}

	// Initialization of the velocity engine
	@Bean
	public VelocityEngine velocityEngine() {
		return VelocityFactory.getEngine();
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public static SAMLBootstrap samlBootstrap() {
		return new SAMLBootstrap();
	}

	// Logger for SAML messages and events
	@Bean
	public SAMLDefaultLogger samlLogger() {
		return new SAMLDefaultLogger();
	}

	// Provider of default SAML Context
	@Bean
	public SAMLContextProviderImpl contextProvider() throws MetadataProviderException {
		SAMLContextProviderImpl sAMLContextProviderImpl = new SAMLContextProviderImpl();
		// MetadataCredentialResolver metadataCredentialResolver = new
		// MetadataCredentialResolver(
		// metadata(), keyManager());
		// metadataCredentialResolver.setUseXmlMetadata(false);
		// sAMLContextProviderImpl.setMetadataResolver(metadataCredentialResolver);
		return sAMLContextProviderImpl;
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public HTTPPAOS11Binding paosBinding() {
		return new HTTPPAOS11Binding(parserPool());
	}

	/**
	 * 
	 * @return
	 */
	@Bean
	public HTTPSOAP11Binding soapBinding() {
		return new HTTPSOAP11Binding(parserPool());
	}

	// Bindings, encoders and decoders used for creating and parsing messages
	@Bean
	public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
		return new MultiThreadedHttpConnectionManager();
	}

	@Bean
	public HttpClient httpClient() {
		return new HttpClient(multiThreadedHttpConnectionManager());
	}

	// Bindings
	private ArtifactResolutionProfile artifactResolutionProfile() {
		final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient());
		artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
		return artifactResolutionProfile;
	}

	// Processor
	@Bean
	public HTTPArtifactBinding httpArtifactBinding() {
		return new HTTPArtifactBinding(parserPool(), velocityEngine(), artifactResolutionProfile());
	}

	@Bean
	public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
		return new HTTPRedirectDeflateBinding(parserPool());
	}

	@Bean
	public HTTPPostBinding httpPostBinding() {
		return new HTTPPostBinding(parserPool(), velocityEngine());
	}

	@Bean
	public SingleLogoutProfile logoutProfile() {
		return new SingleLogoutProfileImpl();
	}

	// SAML 2.0 ECP profile
	@Bean
	public WebSSOProfileECPImpl ecpProfile() {
		return new WebSSOProfileECPImpl();
	}

	// SAML 2.0 Holder-of-Key Web SSO profile (name = "hokWebSSOProfile")
	@Bean
	public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
		return new WebSSOProfileConsumerHoKImpl();
	}

	// SAML 2.0 Web SSO profile
	@Bean
	public WebSSOProfile webSSOprofile() {
		return new WebSSOProfileImpl();
	}

	// SAML 2.0 Holder-of-Key WebSSO Assertion Consumer (name =
	// "hokWebSSOprofileConsumer")
	@Bean
	public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
		return new WebSSOProfileConsumerHoKImpl();
	}

	// SAML 2.0 WebSSO Assertion Consumer
	@Bean
	public WebSSOProfileConsumer webSSOprofileConsumer() {
		WebSSOProfileConsumer result = new WebSSOProfileConsumerImpl();
		return result;
	}

	// Processor
	@Bean
	public SAMLProcessorImpl samlProcessor() {
		Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>();
		bindings.add(httpRedirectDeflateBinding());
		bindings.add(httpPostBinding());
		bindings.add(httpArtifactBinding());
		bindings.add(soapBinding());
		bindings.add(paosBinding());
		return new SAMLProcessorImpl(bindings);
	}

	@Bean
	public WebSSOProfileOptions defaultWebSSOProfileOptions() {
		WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
		webSSOProfileOptions.setIncludeScoping(false);
		// TODO
		// webSSOProfileOptions.setNameID(NameIDType.TRANSIENT);
		// webSSOProfileOptions.setNameID(NameIDType.EMAIL);
		return webSSOProfileOptions;
	}

	// Entry point to initialize authentication, default values taken from
	// properties file
	@Bean
	public SAMLEntryPoint samlEntryPoint() {
		SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
		samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
		return samlEntryPoint;
	}

	/**
	 * Define the security filter chain in order to support SSO Auth by using
	 * SAML 2.0
	 *
	 * @return Filter chain proxy
	 * @throws Exception
	 */
	@Bean
	public FilterChainProxy samlFilter() throws Exception {
		List<SecurityFilterChain> chains = new ArrayList<SecurityFilterChain>();
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
				metadataDisplayFilter()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
				samlWebSSOProcessingFilter()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"),
				samlWebSSOHoKProcessingFilter()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
				samlLogoutProcessingFilter()));
		chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), samlIDPDiscovery()));
		return new FilterChainProxy(chains);
	}

	// IDP Discovery Service
	@Bean
	public SAMLDiscovery samlIDPDiscovery() {
		SAMLDiscovery idpDiscovery = new SAMLDiscovery();
		// idpDiscovery.setIdpSelectionPath("/saml/idpSelection");
		return idpDiscovery;
	}

	@Bean(name = "idp-ssocircle")
	public ExtendedMetadataDelegate metadataProvider() throws MetadataProviderException {

		// TODO
		@SuppressWarnings("deprecation")
		// HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(
		// samlMetadataProvider, 5000);
		// httpMetadataProvider.setParserPool(parserPool());
		Resource resource = new ClassPathResource(samlMetadataProviderFile);

		File metadataFile;
		try {
			metadataFile = resource.getFile();
			logger.info("MetadataFile:{}", metadataFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new MetadataProviderException("metadtaFile" + samlMetadataProviderFile + " is not found");
		}

		FilesystemMetadataProvider httpMetadataProvider = new FilesystemMetadataProvider(metadataFile);
		httpMetadataProvider.setParserPool(parserPool());

		ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(httpMetadataProvider,
				extendedMetadata());
		extendedMetadataDelegate.setMetadataTrustCheck(false);
		extendedMetadataDelegate.setMetadataRequireSignature(false);

		return extendedMetadataDelegate;
	}

	@Bean(name = "metadata")
	public CachingMetadataManager metadata() throws MetadataProviderException {
		List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
		providers.add(metadataProvider());
		return new CachingMetadataManager(providers);
	}

	@Bean
	public MetadataGeneratorFilter metadataGeneratorFilter() {
		return new MetadataGeneratorFilter(metadataGenerator());
	}

	// Central storage of cryptographic keys
	@Bean
	public KeyManager keyManager() {
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource storeFile = loader.getResource("classpath:security/samlKeystore.jks");
		String storePass = "cUtlXNJRoSm28xBgaJmy";
		Map<String, String> passwords = new HashMap<String, String>();
		passwords.put("nmk-key", "Pwy2ytv10NSTTAijZ2wy");
		String defaultKey = "nmk-key";
		return new JKSKeyManager(storeFile, storePass, passwords, defaultKey);
	}

	// Handler for successful logout
	@Bean
	public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
		SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
		successLogoutHandler.setDefaultTargetUrl("/");
		return successLogoutHandler;
	}

	// Logout handler terminating local session
	@Bean
	public SecurityContextLogoutHandler logoutHandler() {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.setInvalidateHttpSession(true);
		logoutHandler.setClearAuthentication(true);
		return logoutHandler;
	}

	// Filter processing incoming logout messages
	// First argument determines URL user will be redirected to after successful
	// global logout
	@Bean
	public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
		return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
	}

	// Overrides default logout processing filter with the one processing SAML
	// messages
	@Bean
	public SAMLLogoutFilter samlLogoutFilter() {
		return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[] { logoutHandler() },
				new LogoutHandler[] { logoutHandler() });
	}

	// Handler deciding where to redirect user after successful login
	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
		SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		successRedirectHandler.setDefaultTargetUrl("/app");
		return successRedirectHandler;
	}

	// Handler deciding where to redirect user after failed login
	@Bean
	public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
		SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
		failureHandler.setUseForward(true);
		failureHandler.setDefaultFailureUrl("/devTools/error.jsp");
		return failureHandler;
	}

	// SAML Authentication Provider responsible for validating of received SAML
	// messages
	@Bean
	public SAMLAuthenticationProvider samlAuthenticationProvider() {
		SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
		samlAuthenticationProvider.setUserDetails(samlSubscriberUserDetailsService);
		samlAuthenticationProvider.setForcePrincipalAsString(false);
		return samlAuthenticationProvider;
	}

	// Processing filter for WebSSO profile messages
	@Bean
	public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
		SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
		samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
		samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
		samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
		return samlWebSSOProcessingFilter;
	}

	@Bean
	public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
		SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
		samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
		samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager());
		samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
		return samlWebSSOHoKProcessingFilter;
	}

	// Setup advanced info about metadata
	@Bean
	public ExtendedMetadata extendedMetadata() {
		ExtendedMetadata extendedMetadata = new ExtendedMetadata();
		extendedMetadata.setIdpDiscoveryEnabled(true);
		// extendedMetadata.setSignMetadata(false);
		// extendedMetadata.setAlias("nmk-p");
		// extendedMetadata.setSigningKey("nmk-key");
		return extendedMetadata;
	}

	// Filter automatically generates default SP metadata
	@Bean
	public MetadataGenerator metadataGenerator() {
		MetadataGenerator metadataGenerator = new MetadataGenerator();
		metadataGenerator.setEntityId(samlEntityId);
		metadataGenerator.setExtendedMetadata(extendedMetadata());
		metadataGenerator.setIncludeDiscoveryExtension(false);
		metadataGenerator.setKeyManager(keyManager());
		return metadataGenerator;
	}

	// The filter is waiting for connections on URL suffixed with filterSuffix
	// and presents SP metadata there
	@Bean
	public MetadataDisplayFilter metadataDisplayFilter() {
		return new MetadataDisplayFilter();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(samlAuthenticationProvider());
		auth.authenticationProvider(userAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.httpBasic().authenticationEntryPoint(samlEntryPoint());

		// выключаем защиту от CSRF атак
		http.csrf().disable();

		http
				.sessionManagement()
				.maximumSessions(5)
				.sessionRegistry(getSessionRegistry())
				.expiredUrl("/login");

		http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class).addFilterAfter(samlFilter(),
				BasicAuthenticationFilter.class);

		// указываем правила запросов
		// по которым будет определятся доступ к ресурсам и остальным
		// данным

		http.authorizeRequests().antMatchers("/").permitAll()
				.antMatchers("/devTools/**").permitAll()
				.antMatchers("/index.html").permitAll()
				.antMatchers("/index").permitAll()
				.antMatchers("/error").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/saml/**").permitAll();

		http.authorizeRequests().antMatchers("/").permitAll()
				.antMatchers("/WEB-INF/**").denyAll()
				.antMatchers("/app/**").authenticated()
				.antMatchers("/api/benchmark/**").permitAll()
				.antMatchers("/api/appStatus/**").permitAll()
				.antMatchers("/api/securityCheck/**").permitAll()
				.antMatchers("/api/**").access(RolesAccess.API_SUBSR_ACCESS) //.authenticated()
				.antMatchers("/api/rma/**").access(RolesAccess.API_RMA_ACCESS)
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/bower_components/**").permitAll()
				.antMatchers("/vendor_components/**").permitAll().and();

		http.formLogin()
				// указываем страницу с формой логина
				.loginPage("/login")
				// указываем action с формы логина
				.loginProcessingUrl("/j_spring_security_check")
				.defaultSuccessUrl("/app", true)
				// указываем URL при неудачном логине
				.failureUrl("/login?error")
				// Указываем параметры логина и пароля с формы логина
				.usernameParameter("j_username").passwordParameter("j_password")
				// даем доступ к форме логина всем
				.permitAll();

		http.logout()
				// разрешаем делать логаут всем
				.permitAll()
				// указываем URL логаута
				.logoutUrl("/local/logout")
				// указываем URL при удачном логауте
				.logoutSuccessUrl("/")
				// делаем не валидной текущую сессию
				.invalidateHttpSession(true);

	}

	public boolean isForceLocalLogin() {
		return forceLocalLogin;
	}

	/**
	 * 
	 * @return
	 */
	@Bean(name = "sessionRegistry")
	public SessionRegistry getSessionRegistry() {
		return new SessionRegistryImpl();
	}
}
