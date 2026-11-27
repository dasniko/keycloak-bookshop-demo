# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

Demo landscape showing different kinds of user and client authentication with Keycloak (realm `bookshop`) across distributed web apps and services. It is a demo, not production code: TLS trust-all, self-signed `localhost` certs and a committed client secret are intentional.

## Build & Run

There are no tests in this repository.

```bash
mvn clean install                 # builds all four modules (CI runs `mvn -B clean install`)
mvn -pl shop quarkus:dev          # run a single Quarkus module in dev mode (also: pim)
mvn -pl cart spring-boot:run      # run the Spring Boot cart service
cd checkout && yarn install && yarn start   # Vite dev server on https://localhost:3000
./deploy.sh                       # mvn clean package + local Docker image build
docker compose up                 # Keycloak (imports bookshop-realm.json) + all-in-one bookshop image
```

- Keycloak for local runs: `http://localhost:8080`, admin/admin (via docker-compose). The realm export contains no human users, so create one in the admin console.
- The `shop` client secret appears in three places that must stay in sync: `bookshop-realm.json`, the `SHOP_CLIENT_SECRET` default in `shop/application.properties`, and `docker-compose.yml`.
- IntelliJ run configs for all four apps are in `.run/`.

## Architecture

Four modules, all served over HTTPS with the self-signed certs in each module's `certs/` or `.tls/` folder:

| Module | Port | Tech | Keycloak role |
|---|---|---|---|
| `shop` | 8081 | Quarkus, Qute templates | Confidential client `shop`: Authorization Code Flow + PKCE, server-side code-to-token exchange, back-channel logout at `/backchannel-logout` |
| `pim` | 8082 | Quarkus | Bearer-only resource server, requires audience `pim` |
| `cart` | 8083 | **Spring Boot** | OAuth2 resource server, requires audience `cart`; cart is keyed by the JWT `sub` |
| `checkout` | 3000 | React + Vite, `keycloak-js` | Public client `checkout` (`public/keycloak.json`), `login-required` |

Token flows. This is the main thing the demo shows, and it spans several files:
- **shop → pim**: `PimClient` uses `@OidcClientFilter`, so it calls pim with a **service-account token** (client credentials of `shop` via `quarkus.oidc-client.*`), not the user's token.
- **shop → cart**: `CartClient` uses `@AccessToken`, so it **propagates the user's access token**.
- **checkout → cart**: the axios interceptor in `checkout/src/index.jsx` refreshes the token (`updateToken(5)`) and sends it as a Bearer token straight to `https://localhost:8083`. The browser helpers `window.setAcrValues()` and `window.setScope()` store ACR or scope in sessionStorage for step-up auth demos.

Cart security details (`cart/.../SecurityConfig.java`): a custom `NimbusJwtDecoder` loads the JWKS from `AUTH_SERVER_URL` (internal URL) with a trust-all HTTP client, but validates the issuer against `AUTH_SERVER_PUBLIC_URL`. That split matters when Keycloak is reachable under different internal and public hostnames, as in Docker. `AudienceValidator` enforces the `aud` claim. `SecurityConfigAlternative` is an inactive variant (its annotations are commented out) that authorizes by the realm role `service-role`.

Key env vars: `AUTH_SERVER_URL`, `AUTH_SERVER_PUBLIC_URL` (also used by shop for the account-console link), `SHOP_CLIENT_ID`, `SHOP_CLIENT_SECRET`, `PIM_AUDIENCE`, `CART_AUDIENCE`.

## HTTP-only mode (GitHub Codespaces)

HTTPS is the default. Setting `HTTP_ONLY=true` in the container makes `docker/entrypoint.sh` switch every app to plain HTTP: it activates the Quarkus profile `http` and the Spring profile `http`, unsets the TLS env vars from the Dockerfile, removes `ssl` from the nginx listener, and writes `config.js` and `keycloak.json` for checkout from `*_PUBLIC_URL`.
- In Quarkus, the HTTPS settings are prefixed with `%dev,prod.`, so they stay off in the `http` profile. Don't move them back to unprefixed keys: the HTTPS listener would then also start in HTTP mode and clash on the ports.
- In the `http` profile, shop builds its OIDC `redirect_uri` from `X-Forwarded-Host` or `Host` and forces the `https` scheme.
- `CHECKOUT_CHECK_LOGIN_IFRAME` (default `true`, any mode) sets keycloak-js `checkLoginIframe` via `config.js`. With `false`, keycloak-js also skips its 3rd-party cookie check iframe (keycloak-js 25, `check3pCookiesSupported`). In Codespaces, that iframe gets GitHub's "Codespaces Access Port" warning page (public port) or the GitHub login (private port), so checkout hangs until the Keycloak URL was opened once directly.
- Browser-facing URLs are configurable instead of host:port: `bookshop.checkout-url` (shop → checkout link), and `window.BOOKSHOP_CONFIG` in `checkout/public/config.js` (`cartUrl`, `shopUrl`). Unset values fall back to the local HTTPS defaults.
- The devcontainer image is pinned to `base:ubuntu-24.04`. Plain `base:ubuntu` resolves to 26.04, where docker-in-docker can't install Docker.
- `.devcontainer/start.sh` runs `docker-compose.codespaces.yml`. It then sets `rootUrl` of the `shop` and `checkout` clients and shop's back-channel logout URL to the Codespaces URLs via `kcadm`, leaving `bookshop-realm.json` untouched. In Codespaces, ports 8080 and 8083 must be public, because the browser calls them cross-origin.
- Codespaces proxy headers, measured with a header-echo server on 2026-11-27: `Host` is rewritten to `localhost:<port>`, but `X-Forwarded-Host` (public host), `X-Forwarded-Proto: https` and `X-Forwarded-Port: 443` are set. That is why shop's forwarded-host setting works. A same-origin `Origin` is rewritten to `http://localhost:<port>`; a foreign `Origin` (e.g. from the checkout port) is passed unchanged.

## Build / packaging layout

- The root `pom.xml` is the parent of `shop`, `pim` and `checkout` and imports the Quarkus BOM (`quarkus.version`). `cart` is listed as a module but uses `spring-boot-starter-parent` as its parent, so Quarkus and root-pom settings do not apply to it.
- `checkout` is built through Maven with `exec-maven-plugin`: `yarn install` at compile and `yarn build` at package, which writes to `checkout/dist`. Building it needs `yarn` on the PATH.
- Quarkus modules package as uber-jars (`*-runner.jar`). `docker/Dockerfile` copies them, the cart jar and `checkout/dist` (served by nginx using `checkout/deployment/nginx.conf`) into a single UBI image. `docker/entrypoint.sh` starts all four processes.
- **Version bumps** touch several files: all `pom.xml` files, `docker/Dockerfile` (`ARG VERSION`), `deploy.sh` (`TAG`), `docker-compose.yml` and `README.adoc`. The Dockerfile's `COPY` paths include the version.
- CI (`.github/workflows/build-and-deploy.yml`) builds on every push. On `main` it also pushes a multi-arch image to `ghcr.io/<repo>:<project.version>`.

## Formatting

`.editorconfig`: tabs (size 2), LF, max line length 140. Some files (e.g. `pim/pom.xml`, parts of `ShopResource.java`) still use spaces; follow the surrounding style when you make small edits.
