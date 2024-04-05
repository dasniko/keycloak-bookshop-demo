package dasniko.keycloak.shop.cart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@SuppressWarnings("unchecked")
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(corsConfig -> corsConfigurationSource())
			.authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("user"))
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtConfigurer -> jwtConfigurer
					.jwtAuthenticationConverter(jwt -> {
						List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").getOrDefault("roles", List.of());
						SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
						Set<GrantedAuthority> authorities = authorityMapper.mapAuthorities(
							roles.stream().map(SimpleGrantedAuthority::new).toList()
						);
						return new JwtAuthenticationToken(jwt, authorities);
					})
				)
			);
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
