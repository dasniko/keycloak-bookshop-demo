package dasniko.keycloak.shop.cart;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
public class CartService {

    Map<String, List<Book>> cart = new HashMap<>();

    List<Book> getCart(String username) {
        return cart.getOrDefault(username, List.of());
    }

    int addToCart(String username, Book book) {
        List<Book> books = cart.getOrDefault(username, new ArrayList<>());
        books.add(book);
        cart.put(username, books);
        return books.size();
    }
}
