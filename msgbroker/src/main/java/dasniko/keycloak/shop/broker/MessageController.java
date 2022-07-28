package dasniko.keycloak.shop.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Slf4j
@RestController
public class MessageController {

    @PostMapping(path = "/msg", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void acceptMessage(@RequestBody String message, @AuthenticationPrincipal Jwt principal) {
        log.info("*".repeat(30 + message.length()));
        log.info("* {}: {}", principal.getClaimAsString("preferred_username"), message);
        log.info("*".repeat(30 + message.length()));
    }

}
