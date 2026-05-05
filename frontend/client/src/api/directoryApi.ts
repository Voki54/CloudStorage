import { dirApi } from "./axios";

const BASE_URL = "/api/directories"

export const createDirectory = async (name: string, parentId: string) => {
  const res = await dirApi.post(BASE_URL, {
    name,
    parentId,
  });

  return res.data;
}

export const getRoot = async () => {
  const res = await dirApi.get(BASE_URL);
  return res.data;
};

export const getDirectoryChildren = async (id: string) => {
  const res = await dirApi.get(`${BASE_URL}/${id}`);
  return res.data;
};