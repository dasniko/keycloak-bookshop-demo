package dasniko.keycloak.shop.checkout;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Controller
public class CheckoutController {

    private static final String CART_URL = "http://localhost:8083/cart";
    private static final String MSG_URL = "http://localhost:8085/msg";

    private final WebClient userWebClient;
    private final WebClient clientWebClient;

    public CheckoutController(@Qualifier(SecurityConfig.USER_WEBCLIENT) WebClient userWebClient,
                              @Qualifier(SecurityConfig.CLIENT_WEBCLIENT) WebClient clientWebClient) {
        this.userWebClient = userWebClient;
        this.clientWebClient = clientWebClient;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/checkout";
    }

    @GetMapping("/checkout")
    public String getCheckout(@AuthenticationPrincipal OidcUser principal, Model model) {
        List<Book> books = getCart();
        model
                .addAttribute("username", principal.getName())
                .addAttribute("books", books);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(@AuthenticationPrincipal OidcUser principal) {
        List<Book> books = getCart();
        String msg = String.format("User %s just bought these books: %s", principal.getName(),
                books.stream().map(Book::getTitle).collect(Collectors.joining(", ")));

        clientWebClient.post()
                .uri(MSG_URL)
                .bodyValue(msg)
                .retrieve().toBodilessEntity()
                .block();

        userWebClient.delete().uri(CART_URL).retrieve().toBodilessEntity().block();
        return "redirect:/checkout";
    }

    private List<Book> getCart() {
        return clientWebClient.get()
                .uri(CART_URL)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Book>>() {})
                .block();
    }

}
