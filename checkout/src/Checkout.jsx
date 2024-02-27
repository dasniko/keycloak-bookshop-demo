import axios from "axios";
import { useEffect, useState } from "react";

const Checkout = ({ kc }) => {

	const [books, setBooks] = useState([]);

	useEffect(() => {
		axios.get('/cart')
			.then((response) => {
				setBooks(response.data);
			})
			.catch(console.error);
	}, []);

	const doCheckout = () => {
		window.alert('Thank you for your order!');
		console.log(`User ${kc.tokenParsed?.preferred_username} just bought these books: ${books.map((b) => b.title).join(', ')}`);
		axios.delete('/cart').then(() => setBooks([]));
	};

	return (
		<>
			<header>
				<nav>
					<img src="/smile.jpg" style={{ height: 70 }} alt="Smile"/>
					<h1>Smiling Bookshop Checkout</h1>
					<ul>
						<li><a href="//localhost:8081/shop">Back to shop</a></li>
						<li>{kc.tokenParsed?.preferred_username}</li>
						<li><a href="#" onClick={() => kc.logout()}>Logout</a></li>
					</ul>
				</nav>
				<h1><i>Ready to checkout your order!?</i></h1>
			</header>
			<main>
				<section>
					{books.length ? (
						<form>
							<table>
								<thead>
								<tr>
									<th>ID</th>
									<th>Title</th>
									<th>Author</th>
								</tr>
								</thead>
								<tbody>
								{books.map((book) => (
									<tr key={book.id}>
										<td>{book.id}</td>
										<td>{book.title}</td>
										<td>{book.author}</td>
									</tr>
								))}
								</tbody>
							</table>
							<div style={{ textAlign: 'right' }}>
								<button type="button" onClick={doCheckout}>Checkout</button>
							</div>
						</form>
					) : (
						<h2><em>
							<mark>No items in cart!</mark>
						</em></h2>
					)}
				</section>
			</main>
			<footer>
				<hr/>
				<p>
					<small>&copy; Niko Köbler | <a href="http://keycloak-experte.de" target="_blank">keycloak-experte.de</a></small>
				</p>
			</footer>
		</>
	);
}

export default Checkout
