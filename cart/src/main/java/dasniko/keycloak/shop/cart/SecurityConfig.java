package dasniko.keycloak.shop.cart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(corsConfig -> corsConfigurationSource())
			.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt -> new JwtAuthenticationToken(jwt, Set.of())))
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

	@Bean
	public JwtDecoder jwtDecoder(
			@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
			@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
			@Value("${spring.security.oauth2.resourceserver.jwt.audience}") String audience) {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
				.jwsAlgorithms(algorithms -> {
					// Support multiple signature algorithms dynamically
					// RS256, RS384, RS512 (RSA with SHA-256, SHA-384, SHA-512)
					algorithms.add(SignatureAlgorithm.RS256);
					algorithms.add(SignatureAlgorithm.RS384);
					algorithms.add(SignatureAlgorithm.RS512);
					// PS256, PS384, PS512 (RSA-PSS with SHA-256, SHA-384, SHA-512)
					algorithms.add(SignatureAlgorithm.PS256);
					algorithms.add(SignatureAlgorithm.PS384);
					algorithms.add(SignatureAlgorithm.PS512);
					// ES256, ES384, ES512 (ECDSA with SHA-256, SHA-384, SHA-512)
					algorithms.add(SignatureAlgorithm.ES256);
					algorithms.add(SignatureAlgorithm.ES384);
					algorithms.add(SignatureAlgorithm.ES512);
				})
				.build();

		OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

		jwtDecoder.setJwtValidator(withAudience);
		return jwtDecoder;
	}

}
