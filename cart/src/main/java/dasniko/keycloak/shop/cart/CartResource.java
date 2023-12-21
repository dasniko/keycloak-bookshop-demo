package dasniko.keycloak.shop.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@RestController
@RequestMapping(path = "cart", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartResource {

	private final CartService cartService;

	@Autowired
	public CartResource(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Integer> addToCart(@RequestBody Book book, @AuthenticationPrincipal Jwt jwt) {
		String subject = jwt.getSubject();
		int size = cartService.addToCart(subject, book);
		return Map.of("size", size);
	}

	@GetMapping
	public List<Book> getCart(@AuthenticationPrincipal Jwt jwt) {
		String subject = jwt.getSubject();
		return cartService.getCart(subject);
	}

	@DeleteMapping
	public void deleteCart(@AuthenticationPrincipal Jwt jwt) {
		String subject = jwt.getSubject();
		cartService.deleteCart(subject);
	}

}
