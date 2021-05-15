package dasniko.keycloak.shop;

import io.quarkus.oidc.token.propagation.AccessToken;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@RegisterRestClient
@Path("/cart")
@AccessToken
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CartClient {

    @POST
    Map<String, Integer> addToCart(Book book);

    @GET
    List<Book> getCart();

}
