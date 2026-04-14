import './index.css'
import App from './App.tsx'
import keycloak from "./keycloak";
import React from 'react';
import ReactDOM from 'react-dom/client'

// createRoot(document.getElementById('root')!).render(
//   <StrictMode>
//     <App />
//   </StrictMode>,
// )

keycloak
  .init({
    onLoad: "login-required",
    checkLoginIframe: false,
  })
  .then((authenticated) => {
    if (!authenticated) {
      console.log("Not authenticated");
    }

    ReactDOM.createRoot(document.getElementById("root")!).render(
      <React.StrictMode>
        <App />
      </React.StrictMode>
    );
  })
  .catch((err) => {
    console.error("Keycloak init error", err);
  });

  // Автообновление токена
//   setInterval(() => {
//     keycloak.updateToken(30).catch(() => {
//       console.log("Failed to refresh token");
//     });
//   }, 10000);