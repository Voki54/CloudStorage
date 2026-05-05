import axios from "axios";
import type { AxiosInstance } from "axios";
import keycloak from "../keycloak";

export const fileApi = axios.create({
  baseURL: "http://localhost:8081",
});

export const dirApi = axios.create({
  baseURL: "http://localhost:8082",
});

const setupInterceptors = (api: AxiosInstance) => {
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
};

setupInterceptors(fileApi);
setupInterceptors(dirApi);