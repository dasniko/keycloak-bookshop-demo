package dasniko.keycloak.shop.pim;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Path("catalogue")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class CatalogueResource {

    @GET
    public List<Book> getBooks() {
        return List.of(
                new Book(1, "Brave New World", "Aldous Huxley"),
                new Book(2, "Pride and Prejudice", "Jane Austen"),
                new Book(3, "Don Quixote", "Miguel de Cervantes Saavedra"),
                new Book(4, "1984", "George Orwell"),
                new Book(5, "Gone With The Wind", "Margaret Mitchell"),
                new Book(6, "The Great Gatsby", "F Scott Fitzgerald"),
                new Book(7, "Life of Pi", "Yann Martel"),
                new Book(8, "Oliver Twist", "Charles Dickens"),
                new Book(9, "Adventures of Huckleberry Finn", "Mark Twain"),
                new Book(10, "The Old Man and the Sea", "Ernest Hemingway")
        );
    }
}
