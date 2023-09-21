import Footer from "./Footer";

const StepUp = ({ kc }) => (
	<>
		<header>
			<nav>
				<h1>Step-Up Authentication Demo</h1>
				<ul>
					<li>{kc.tokenParsed?.preferred_username}</li>
					<li><a href="#" onClick={() => kc.logout()}>Logout</a></li>
				</ul>
			</nav>
		</header>
		<main>
			<section>
				<aside>
					<h3>ACR Value from Token</h3>
					<p><mark>{kc.tokenParsed?.acr}</mark></p>
				</aside>
				{kc.tokenParsed?.acr !== 'gold' && (
				<aside>
					<h3>Step-Up</h3>
					<a href="#" onClick={() => kc.login({ acr: { values: ['gold'], essential: true } })}>
						<b>Gold</b>
					</a>
				</aside>
				)}
				<aside style={{ width: '75%' }}>
					<details>
						<summary>Access Token</summary>
						<p>
							<pre><code>{JSON.stringify(kc.tokenParsed, null, 2)}</code></pre>
						</p>
					</details>
				</aside>
			</section>
		</main>
		<Footer/>
	</>
)

export default StepUp
