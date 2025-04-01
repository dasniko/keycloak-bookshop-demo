package dasniko.keycloak.shop.cart;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//@Configuration
//@EnableWebSecurity
@SuppressWarnings("unused")
public class SecurityConfigAlternative extends SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(corsConfig -> corsConfigurationSource())
			.authorizeHttpRequests(authorize -> authorize.anyRequest().hasAuthority("service-role"))
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt ->
					new JwtAuthenticationToken(jwt, extractedAuthorities(jwt))))
			);
		return http.build();
	}

	@SuppressWarnings("unchecked")
	private Collection<GrantedAuthority> extractedAuthorities(Jwt jwt) {
		Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
		if (realmAccess != null && realmAccess.containsKey("roles")) {
			return realmAccess.get("roles").stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
		}
		return Set.of();
	}

}
