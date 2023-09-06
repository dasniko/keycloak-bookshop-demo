import Checkout from "./Checkout";
import StepUp from "./StepUp";

const App = ({ kc }) => (
	document.cookie.includes('showStepUp')
		? <StepUp kc={kc}/>
		: <Checkout kc={kc}/>
)

export default App
