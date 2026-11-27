#!/bin/bash

# HTTP-only mode, e.g. for GitHub Codespaces, where the Codespaces proxy terminates TLS
# and every app is reachable under its own public HTTPS URL.
if [ "${HTTP_ONLY:-false}" = "true" ]; then
	: "${AUTH_SERVER_PUBLIC_URL:?AUTH_SERVER_PUBLIC_URL is required in HTTP-only mode}"
	: "${SHOP_PUBLIC_URL:?SHOP_PUBLIC_URL is required in HTTP-only mode}"
	: "${CART_PUBLIC_URL:?CART_PUBLIC_URL is required in HTTP-only mode}"
	: "${CHECKOUT_PUBLIC_URL:?CHECKOUT_PUBLIC_URL is required in HTTP-only mode}"

	echo "Configuring apps for HTTP-only mode..."
	unset QUARKUS_HTTP_SSL_CERTIFICATE_FILES QUARKUS_HTTP_SSL_CERTIFICATE_KEY_FILES
	export QUARKUS_PROFILE=http
	export SPRING_PROFILES_ACTIVE=http

	sed -i 's/listen\( *\)3000 ssl default_server;/listen\13000 default_server;/' /etc/nginx/nginx.conf

	cat > /usr/share/nginx/html/keycloak.json <<EOF
{
	"realm": "bookshop",
	"auth-server-url": "${AUTH_SERVER_PUBLIC_URL%/realms/*}/",
	"resource": "checkout",
	"public-client": true
}
EOF
fi

# Runtime configuration of the checkout app (see checkout/public/config.js).
# CHECKOUT_CHECK_LOGIN_IFRAME=false disables keycloak-js' login status iframe (and its 3rd-party cookie check iframe),
# e.g. for GitHub Codespaces, where a public port answers the iframe with a warning page that can't be confirmed.
CHECK_LOGIN_IFRAME="${CHECKOUT_CHECK_LOGIN_IFRAME:-true}"
case "$CHECK_LOGIN_IFRAME" in
	true|false) ;;
	*) echo "CHECKOUT_CHECK_LOGIN_IFRAME must be true or false" >&2; exit 1 ;;
esac
js_string() {
	if [ -n "$1" ]; then printf "'%s'" "$1"; else printf 'undefined'; fi
}
cat > /usr/share/nginx/html/config.js <<EOF
window.BOOKSHOP_CONFIG = {
	cartUrl: $(js_string "${CART_PUBLIC_URL:-}"),
	shopUrl: $(js_string "${SHOP_PUBLIC_URL:-}"),
	checkLoginIframe: ${CHECK_LOGIN_IFRAME},
};
EOF

echo "Starting apps..."
QUARKUS_JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
java -jar /deployments/pim.jar "$QUARKUS_JAVA_OPTS" &
java -jar /deployments/shop.jar "$QUARKUS_JAVA_OPTS" &
java -jar /deployments/cart.jar &
nginx -g 'daemon off;' &

trap "exit" SIGINT
trap "exit" SIGTERM

wait -n
exit $?
