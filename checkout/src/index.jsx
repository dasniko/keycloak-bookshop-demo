import axios from "axios";
import Keycloak from "keycloak-js";
import { createRoot } from "react-dom/client";
import App from "./App";

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
	pkceMethod: 'S256',
})
	.then((authenticated) => {
		if (!authenticated) {
			console.log("user is not authenticated..!");
		} else {
			createRoot(document.getElementById("app")).render(<App kc={kc}/>)
		}
	})
	.catch(console.error);