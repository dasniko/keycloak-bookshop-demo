package dasniko.keycloak.shop;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Path("shop")
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class ShopResource {

    @Inject
    JsonWebToken accessToken;

    @Inject
    @RestClient
    PimClient pimClient;
    @Inject
    @RestClient
    CartClient cartClient;

    @Inject
    Template shop;

    @GET
    public TemplateInstance showProducts() {
        String username = accessToken.getName();
        List<Book> books = pimClient.getBooks();
        int cartSize = cartClient.getCart().size();
        return shop
                .data("user", username)
                .data("books", books)
                .data("cartSize", cartSize);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addToCart(Book book) {
        Map<String, Integer> map = cartClient.addToCart(book);
        return Response.ok(map).build();
    }
}
