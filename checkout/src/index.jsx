import axios from "axios";
import Keycloak from "keycloak-js";
import { createRoot } from "react-dom/client";
import Checkout from "./Checkout";

const kc = new Keycloak('/keycloak.json');


// HTTP

axios.defaults.baseURL = 'http://localhost:8083';
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
	useNonce: true,
	pkceMethod: 'S256',
	enableLogging: true,
	acrValues: document.cookie.split(';').find((e) => e.startsWith('acr_values='))?.split('=')[1],
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
	document.cookie = `acr_values=${acrValues}` + (!acrValues ? ';max-age=-1' : '');
	console.log(`Set acr_values = '${acrValues}'`);
}
