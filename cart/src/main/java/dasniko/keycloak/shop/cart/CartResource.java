package dasniko.keycloak.shop.cart;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
