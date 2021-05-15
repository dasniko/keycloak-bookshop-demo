package dasniko.keycloak.shop;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
