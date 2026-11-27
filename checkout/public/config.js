// Runtime configuration of the checkout app.
// The Docker entrypoint overwrites this file from env vars (see docker/entrypoint.sh).
// Unset values fall back to the local HTTPS defaults (see src/index.jsx and src/Checkout.jsx).
window.BOOKSHOP_CONFIG = {
	cartUrl: undefined,
	shopUrl: undefined,
	checkLoginIframe: true,
};
