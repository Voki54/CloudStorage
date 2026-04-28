import axios from "axios";
import keycloak from "../keycloak";

const api = axios.create({
  baseURL: "http://localhost:8081",
});

api.interceptors.request.use(async (config) => {
  try {
    await keycloak.updateToken(30);

    config.headers.Authorization = `Bearer ${keycloak.token}`;
  } catch (e) {
    console.error("Token refresh failed", e);
    keycloak.login();
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      keycloak.login();
    }

    return Promise.reject(error);
  }
);

export default api;