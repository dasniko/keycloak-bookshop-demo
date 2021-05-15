package dasniko.keycloak.shop;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
@RegisterRestClient
@OidcClientFilter
@Consumes(MediaType.APPLICATION_JSON)
public interface PimClient {

    @GET
    @Path("/catalogue")
    List<Book> getBooks();
}
