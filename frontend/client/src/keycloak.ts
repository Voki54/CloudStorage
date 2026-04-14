import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "cloud-storage",
  clientId: "frontend",
});

export default keycloak;