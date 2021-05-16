package dasniko.keycloak.shop.checkout;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Configuration
public class OAuth2RestTemplateConfig {

    public static final String OAUTH_2_WEBCLIENT = "OAUTH2_WEBCLIENT";

    private final RestTemplateBuilder restTemplateBuilder;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2RestTemplateConfig(RestTemplateBuilder restTemplateBuilder,
                                    OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                    ClientRegistrationRepository clientRegistrationRepository) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;

    }

    @Bean(OAUTH_2_WEBCLIENT)
    RestTemplate oauth2RestTemplate() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("keycloak");

        return restTemplateBuilder
                .additionalInterceptors(new OAuth2ClientCredentialsRestTemplateInterceptor(authorizedClientManager(), clientRegistration))
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager() {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
