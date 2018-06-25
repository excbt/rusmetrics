package ru.excbt.datafuse.nmk.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.PortalProperties;
import ru.excbt.datafuse.nmk.data.model.UserPersistentToken;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.data.repository.UserPersistentTokenRepository;
import ru.excbt.datafuse.nmk.data.repository.V_FullUserInfoRepository;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p>
 * This is a specific implementation of Spring Security's remember-me authentication, but it is much
 * more powerful than the standard implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and invalidate them</li>
 * <li>It stores more information, such as the IP address and the user agent, for audit purposes<li>
 * <li>When a user logs out, only his current session is invalidated, and not all of his sessions</li>
 * </ul>
 * <p>
 * This is inspired by:
 * <ul>
 * <li><a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved Persistent Login Cookie
 * Best Practice</a></li>
 * <li><a href="https://github.com/blog/1661-modeling-your-app-s-user-session">Github's "Modeling your App's User Session"</a></li>
 * </ul>
 * <p>
 * The main algorithm comes from Spring Security's PersistentTokenBasedRememberMeServices, but this class
 * couldn't be cleanly extended.
 */
@Service
public class CustomPersistentRememberMeServices extends
    AbstractRememberMeServices {

    private final Logger log = LoggerFactory.getLogger(CustomPersistentRememberMeServices.class);

    // Token is valid for one month
    private static final int TOKEN_VALIDITY_DAYS = 31;

    private static final int TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS;

    private static final int DEFAULT_SERIES_LENGTH = 16;

    private static final int DEFAULT_TOKEN_LENGTH = 16;

    private SecureRandom random;


    private final UserPersistentTokenRepository persistentTokenRepository;

    private final V_FullUserInfoRepository userRepository;

    @Autowired
    public CustomPersistentRememberMeServices(PortalProperties portalProperties,
                                              org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
                                              UserPersistentTokenRepository persistentTokenRepository,
                                              V_FullUserInfoRepository subscrUserRepository) {

        super(portalProperties.getSecurity().getRememberMe().getKey(), userDetailsService);
        this.persistentTokenRepository = persistentTokenRepository;
        this.userRepository = subscrUserRepository;
        random = new SecureRandom();
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {


        UserPersistentToken token;
        Optional<V_FullUserInfo> userOpt;
        try {
            token = getPersistentToken(cookieTokens);
            userOpt = userRepository.findById(token.getUserId());
        } catch (CookieTheftException e) {
            // We catch CookieTheftException and throw RememberMeAuthenticationException,
            // because CookieTheftException doesn't redirect to login form
            log.error("CookieTheftException: {}", e);
            throw new RememberMeAuthenticationException("Autologin failed due to token problem");
        }


        if (!userOpt.isPresent()) {
            log.error("User with ID: {} is not found in V_FullUserInfo", token.getUserId());
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        String login = userOpt.get().getUserName();

        // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
        log.debug("Refreshing persistent login token for user '{}', series '{}'", login, token.getSeries());
        token.setTokenDate(LocalDate.now());
        token.setTokenValue(generateTokenData());
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        try {
            persistentTokenRepository.saveAndFlush(token);
            addCookie(token, request, response);
        } catch (DataAccessException e) {
            log.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
        }
        return getUserDetailsService().loadUserByUsername(login);
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication
        successfulAuthentication) {

        String login = successfulAuthentication.getName();

        log.debug("Creating new persistent login for user {}", login);
        UserPersistentToken token = userRepository.findOneByUserNameIgnoreCase(login).map(u -> {
            UserPersistentToken t = new UserPersistentToken();
            t.setSeries(generateSeriesData());
            t.setUserId(u.getId());
            t.setTokenValue(generateTokenData());
            t.setTokenDate(LocalDate.now());
            t.setIpAddress(request.getRemoteAddr());
            t.setUserAgent(request.getHeader("User-Agent"));
            return t;
        }).orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
        try {
            persistentTokenRepository.saveAndFlush(token);
            addCookie(token, request, response);
        } catch (DataAccessException e) {
            log.error("Failed to save persistent token ", e);
        }
    }

    /**
     * When logout occurs, only invalidate the current token, and not all user sessions.
     * <p>
     * The standard Spring Security implementations are too basic: they invalidate all tokens for the
     * current user, so when he logs out from one browser, all his other sessions are destroyed.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                String[] cookieTokens = decodeCookie(rememberMeCookie);
                UserPersistentToken token = getPersistentToken(cookieTokens);
                persistentTokenRepository.delete(token);
            } catch (InvalidCookieException ice) {
                log.info("Invalid cookie, no persistent token could be deleted", ice);
            } catch (RememberMeAuthenticationException rmae) {
                log.debug("No persistent token found, so no token could be deleted", rmae);
            }
        }
        super.logout(request, response, authentication);
    }

    /**
     * Validate the token and return it.
     */
    private UserPersistentToken getPersistentToken(String[] cookieTokens) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        Optional<UserPersistentToken> tokenOpt = persistentTokenRepository.findById(presentedSeries);

        if (!tokenOpt.isPresent()) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        //log.info("presentedToken={} / tokenValue={}", presentedToken, token.getTokenValue());
        if (!presentedToken.equals(tokenOpt.get().getTokenValue())) {
            // Token doesn't match series value. Delete this session and throw an exception.
            persistentTokenRepository.delete(tokenOpt.get());
            throw new CookieTheftException("Invalid remember-me token (Series/token) mismatch. Implies previous " +
                "cookie theft attack.");
        }

        if (tokenOpt.get().getTokenDate().plusDays(TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
            persistentTokenRepository.delete(tokenOpt.get());
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return tokenOpt.get();
    }

    private String generateSeriesData() {
        byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
        random.nextBytes(newSeries);
        return new String(Base64.encode(newSeries));
    }

    private String generateTokenData() {
        byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
        random.nextBytes(newToken);
        return new String(Base64.encode(newToken));
    }

    private void addCookie(UserPersistentToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(
            new String[]{token.getSeries(), token.getTokenValue()},
            TOKEN_VALIDITY_SECONDS, request, response);
    }

    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
      return super.rememberMeRequested(request, parameter);
    }
}
