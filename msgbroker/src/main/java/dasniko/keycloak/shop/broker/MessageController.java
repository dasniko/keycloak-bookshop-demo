package dasniko.keycloak.shop.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@RestController
public class MessageController {

    Logger logger = LoggerFactory.getLogger(MessageController.class);

    @PostMapping(path = "/msg", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void acceptMessage(@RequestBody String message, @AuthenticationPrincipal Jwt principal) {
        logger.info("*".repeat(30 + message.length()));
        logger.info("* {}: {}", principal.getClaimAsString("preferred_username"), message);
        logger.info("*".repeat(30 + message.length()));
    }

}
