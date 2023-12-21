package dasniko.keycloak.shop.cart;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Service
public class CartService {

    Map<String, List<Book>> cart = new HashMap<>();

    List<Book> getCart(String userId) {
        return cart.getOrDefault(userId, List.of());
    }

    int addToCart(String userId, Book book) {
        List<Book> books = cart.getOrDefault(userId, new ArrayList<>());
        books.add(book);
        cart.put(userId, books);
        return books.size();
    }

    public void deleteCart(String userId) {
        cart.remove(userId);
    }
}
