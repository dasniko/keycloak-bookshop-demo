// Runtime configuration of the checkout app.
// The Docker entrypoint overwrites this file in HTTP-only mode (HTTP_ONLY=true), e.g. for GitHub Codespaces.
// Unset values fall back to the local HTTPS defaults (see src/index.jsx and src/Checkout.jsx).
window.BOOKSHOP_CONFIG = {
	cartUrl: undefined,
	shopUrl: undefined,
};
