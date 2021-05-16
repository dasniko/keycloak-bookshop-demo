package dasniko.keycloak.shop.broker;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/msg")
                .authorizeRequests()
                .anyRequest().hasRole("serviceAccount")
                .and()
                .oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter());
    }

    static class KeycloakJwtAuthenticationConverter extends JwtAuthenticationConverter {

        @Override
        @SuppressWarnings({"unchecked", "deprecation"})
        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
            return ((Collection<String>) jwt.getClaimAsMap("realm_access").get("roles"))
                    .stream().map(role -> "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }
}
