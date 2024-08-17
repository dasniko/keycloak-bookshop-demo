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
			<header className="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom">
				<a href="/" className="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
					<img src="/smile.jpg" className="me-3" style={{ width: 100 }} alt=""/>
					<span className="fs-3 fw-semibold text-body-emphasis">Smiling Bookshop Checkout</span>
				</a>
				<ul className="nav nav-pills">
					<li className="nav-item"><a href="//localhost:8081/shop" className="nav-link">Back to shop</a></li>
					<li className="nav-item nav-link mx-3 text-body">{kc.tokenParsed?.preferred_username}</li>
					<li className="nav-item"><a href="#" onClick={() => kc.logout()} className="nav-link">Logout</a></li>
				</ul>
			</header>
			<main className="mx-auto my-5" style={{ width: 600 }}>
				<h2 className="my-5"><i>Ready to checkout your order!?</i></h2>
				{books.length ? (
					<form>
						<table className="table">
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
							<button type="button" className="btn btn-danger" onClick={doCheckout}>Checkout</button>
						</div>
					</form>
				) : (
					<div className="badge text-bg-warning fs-5">No items in cart!</div>
				)}
			</main>
			<footer>
				<hr/>
				<p className="text-muted">
					<small>&copy; Niko Köbler | <a href="http://keycloak-experte.de" target="_blank">keycloak-experte.de</a></small>
				</p>
			</footer>
		</>
	);
}

export default Checkout
