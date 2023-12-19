package dasniko.keycloak.shop;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@ApplicationScoped
@RegisterRestClient(configKey = "pim")
@OidcClientFilter
@Consumes(MediaType.APPLICATION_JSON)
public interface PimClient {

    @GET
    @Path("/catalogue")
    List<Book> getBooks();
}
