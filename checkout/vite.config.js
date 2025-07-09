import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import fs from 'node:fs';

// https://vitejs.dev/config/
export default defineConfig(() => {
	return {
		plugins: [react()],
		server: {
			https: {
				cert: fs.readFileSync('.tls/localhost.crt'),
				key: fs.readFileSync('.tls/localhost.key'),
			},
			open: true,
			port: 3000,
			strictPort: true,
		}
	};
});
