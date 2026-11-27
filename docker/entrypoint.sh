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

	cat > /usr/share/nginx/html/config.js <<EOF
window.BOOKSHOP_CONFIG = {
	cartUrl: '${CART_PUBLIC_URL}',
	shopUrl: '${SHOP_PUBLIC_URL}',
};
EOF
	cat > /usr/share/nginx/html/keycloak.json <<EOF
{
	"realm": "bookshop",
	"auth-server-url": "${AUTH_SERVER_PUBLIC_URL%/realms/*}/",
	"resource": "checkout",
	"public-client": true
}
EOF
fi

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
