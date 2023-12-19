package dasniko.keycloak.shop;

import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@RegisterRestClient(configKey = "cart")
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
