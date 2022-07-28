package dasniko.keycloak.shop.checkout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public static final String USER_WEBCLIENT = "userWebclient";
    public static final String CLIENT_WEBCLIENT = "clientWebclient";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
                .authorizeRequests(authzRequests -> authzRequests.antMatchers("/checkout*").hasRole("user").anyRequest().permitAll())
                .logout(l -> l.logoutSuccessHandler(logoutSuccessHandler))
                .oauth2Login();
        return http.build();
    }

    @Bean
    GrantedAuthoritiesMapper authoritiesMapper() {
        return authorities -> {
            Set<String> roles = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    roles.addAll(extractRoles(oidcUserAuthority.getIdToken().getClaims()));
                    roles.addAll(extractRoles(oidcUserAuthority.getUserInfo().getClaims()));
                } else if (authority instanceof OAuth2UserAuthority oAuth2UserAuthority) {
                    roles.addAll(extractRoles(oAuth2UserAuthority.getAttributes()));
                }
            });
            return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toSet());
        };
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Map<String, Object> claims) {
        Map<String, List<String>> realmAccess = (Map<String, List<String>>) claims.get("realm_access");
        return realmAccess != null ? realmAccess.get("roles") : List.of();
    }

    @Bean
    LogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    ReactiveClientRegistrationRepository clientRegistrations(ClientRegistrationRepository clientRegistrationRepository) {
        InMemoryClientRegistrationRepository repository = (InMemoryClientRegistrationRepository) clientRegistrationRepository;
        List<ClientRegistration> registrations = new ArrayList<>();
        repository.iterator().forEachRemaining(registrations::add);
        return new InMemoryReactiveClientRegistrationRepository(registrations);
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository clientRepository(ReactiveClientRegistrationRepository clientRegistrations) {
        var authorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
        return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Bean(CLIENT_WEBCLIENT)
    WebClient clientWebClient(@Value("${client.registration.name}") String registrationName, ReactiveClientRegistrationRepository clientRegistrations) {
        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
                new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
        oauth.setDefaultClientRegistrationId(registrationName);

        return WebClient.builder()
                .filter(oauth)
                .build();
    }

    @Bean(USER_WEBCLIENT)
    WebClient userWebClient(@Value("${app.registration.name}") String registrationName,
                            ReactiveClientRegistrationRepository clientRegistrations,
                            ServerOAuth2AuthorizedClientRepository authorizedClients) {
        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
        oauth.setDefaultOAuth2AuthorizedClient(true);
        oauth.setDefaultClientRegistrationId(registrationName);

        return WebClient.builder()
                .filter(oauth)
                .build();
    }

}
