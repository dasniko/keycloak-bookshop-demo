import axios from "axios";
import Keycloak from "keycloak-js";
import { createRoot } from "react-dom/client";
import Checkout from "./Checkout";
import 'bootstrap/dist/css/bootstrap.css'

const kc = new Keycloak('/keycloak.json');


// HTTP

axios.defaults.baseURL = 'https://localhost:8083';
axios.interceptors.request.use((config) =>
	kc.updateToken(5)
		.then(() => {
			config.headers.Authorization = `Bearer ${kc.token}`;
			return Promise.resolve(config);
		})
		.catch(kc.login)
);


// APP

kc.init({
	onLoad: 'login-required',
	enableLogging: true,
	acrValues: window.sessionStorage.getItem('ACR'),
	scope: window.sessionStorage.getItem('scope') || undefined,
})
	.then((authenticated) => {
		if (!authenticated) {
			console.log("user is not authenticated..!");
		} else {
			createRoot(document.getElementById("app")).render(<Checkout kc={kc}/>)
		}
	})
	.catch(console.error);


window.setAcrValues = (acrValues) => {
	if (acrValues) {
		window.sessionStorage.setItem('ACR', acrValues);
	} else {
		window.sessionStorage.removeItem("ACR");
	}
}

window.setScope = (scope) => {
	if (scope) {
		window.sessionStorage.setItem("scope", scope);
	} else {
		window.sessionStorage.removeItem("scope")
	}
}
