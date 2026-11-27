#!/usr/bin/env bash
# Starts the HTTP-only bookshop setup in a GitHub Codespace and points the Keycloak clients
# to the public Codespaces URLs (the imported realm is configured for https://localhost).
set -euo pipefail
cd "$(dirname "$0")/.."

: "${CODESPACE_NAME:?not running in a GitHub Codespace}"
: "${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN:?not running in a GitHub Codespace}"

# The Keycloak port has to be public in Codespaces, so don't use a well-known admin password.
# Keycloak applies the bootstrap admin only on first start, so the password is kept for later starts.
PASSWORD_FILE=.devcontainer/.kc-admin-password
if [ ! -s "$PASSWORD_FILE" ]; then
	(umask 077 && openssl rand -base64 18 > "$PASSWORD_FILE")
fi
export KC_ADMIN_PASSWORD="$(cat "$PASSWORD_FILE")"

COMPOSE=(docker compose -f docker-compose.codespaces.yml)
"${COMPOSE[@]}" up -d

public_url() {
	echo "https://${CODESPACE_NAME}-$1.${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN}"
}

kcadm() {
	"${COMPOSE[@]}" exec -T keycloak /opt/keycloak/bin/kcadm.sh "$@"
}

echo "Waiting for Keycloak..."
for attempt in $(seq 1 60); do
	if kcadm config credentials --server http://localhost:8080 --realm master --user admin --password "$KC_ADMIN_PASSWORD" >/dev/null 2>&1; then
		break
	fi
	if [ "$attempt" -eq 60 ]; then
		echo "Keycloak did not become ready in time." >&2
		exit 1
	fi
	sleep 3
done

client_id() {
	kcadm get clients -r bookshop -q clientId="$1" --fields id --format csv --noquotes | tr -d '\r'
}

echo "Configuring Keycloak clients for the Codespaces URLs..."
kcadm update "clients/$(client_id shop)" -r bookshop \
	-s rootUrl="$(public_url 8081)" \
	-s 'attributes."backchannel.logout.url"=http://bookshop:8081/backchannel-logout'
kcadm update "clients/$(client_id checkout)" -r bookshop \
	-s rootUrl="$(public_url 3000)"

cat <<INFO

Bookshop is starting:
  Shop:     $(public_url 8081)/shop
  Checkout: $(public_url 3000)
  Keycloak: $(public_url 8080) (admin password in $PASSWORD_FILE)

The browser calls Keycloak (8080) and the cart (8083) cross-origin, so these ports must be public:
  gh codespace ports visibility 8080:public 8083:public -c "\$CODESPACE_NAME"
INFO
