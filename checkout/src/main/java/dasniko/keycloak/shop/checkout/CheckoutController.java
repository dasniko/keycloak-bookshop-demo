package dasniko.keycloak.shop.checkout;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Controller
@SuppressWarnings({"unchecked", "rawtypes"})
public class CheckoutController {

    private static final String CART_URL = "http://localhost:8083/cart";

    private final KeycloakRestTemplate keycloakRestTemplate;

    @Autowired
    public CheckoutController(KeycloakRestTemplate keycloakRestTemplate) {
        this.keycloakRestTemplate = keycloakRestTemplate;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/checkout";
    }

    @GetMapping("/checkout")
    public String getCheckout(Principal principal, Model model) {
        ResponseEntity<List> response = keycloakRestTemplate.getForEntity(CART_URL, List.class);
        List<Book> books = response.getBody();
        model
                .addAttribute("username", principal.getName())
                .addAttribute("books", books);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(Principal principal) {
        keycloakRestTemplate.delete(CART_URL);
        return "redirect:/checkout";
    }

}
