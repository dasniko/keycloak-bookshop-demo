package dasniko.keycloak.shop.cart;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Path("cart")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class CartResource {

    @Inject
    JsonWebToken accessToken;

    @Inject
    CartService cartService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addToCart(Book book) {
        String username = accessToken.getName();
        int size = cartService.addToCart(username, book);
        return Response.ok(Map.of("size", size)).build();
    }

    @GET
    public Response getCart() {
        String username = accessToken.getName();
        List<Book> books = cartService.getCart(username);
        return Response.ok(books).build();
    }

    @DELETE
    public Response deleteCart() {
        String username = accessToken.getName();
        cartService.deleteCart(username);
        return Response.noContent().build();
    }

}
