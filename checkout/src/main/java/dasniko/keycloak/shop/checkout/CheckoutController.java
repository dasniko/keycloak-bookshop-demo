package dasniko.keycloak.shop.checkout;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Controller
public class CheckoutController {

    private static final String CART_URL = "http://localhost:8083/cart";
    private static final String MSG_URL = "http://localhost:8085/msg";

    private final KeycloakRestTemplate keycloakRestTemplate;
    private final RestTemplate oauth2ClientRestTemplate;

    @Autowired
    public CheckoutController(KeycloakRestTemplate keycloakRestTemplate,
                              @Qualifier(OAuth2RestTemplateConfig.OAUTH_2_WEBCLIENT) RestTemplate oauth2ClientRestTemplate) {
        this.keycloakRestTemplate = keycloakRestTemplate;
        this.oauth2ClientRestTemplate = oauth2ClientRestTemplate;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/checkout";
    }

    @GetMapping("/checkout")
    public String getCheckout(Principal principal, Model model) {
        List<Book> books = getCart();
        model
                .addAttribute("username", principal.getName())
                .addAttribute("books", books);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(Principal principal) {
        List<Book> books = getCart();
        String msg = String.format("User %s just bought these books: %s", principal.getName(),
                books.stream().map(Book::getTitle).collect(Collectors.joining(", ")));

        oauth2ClientRestTemplate.postForEntity(MSG_URL, msg, Void.class);

        keycloakRestTemplate.delete(CART_URL);
        return "redirect:/checkout";
    }

    private List<Book> getCart() {
        ResponseEntity<List<Book>> response = keycloakRestTemplate.exchange(CART_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

}
