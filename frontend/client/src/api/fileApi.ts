import { fileApi } from "./axios";

const BASE_URL = "/api/files"

export const getFiles = async () => {
  const res = await fileApi.get(BASE_URL);
  return res.data;
};

export const getFilesByDirectory = async (directoryId: string) => {
  const res = await fileApi.get(BASE_URL, {
    params: { directoryId }
  });
  return res.data;
};

export const getFileDetails = async (id: string) => {
  const res = await fileApi.get(`${BASE_URL}/${id}`);
  return res.data;
};

export const uploadFile = async (file: File, directoryId: string) => {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("directoryId", directoryId);

  const res = await fileApi.post(BASE_URL, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return res.data;
};

export const downloadFile = async (id: string) => {
  const res = await fileApi.get(`${BASE_URL}/${id}/download`, {
    responseType: "blob",
  });

  return res;
};

export const deleteFile = async (id: string) => {
  await fileApi.delete(`${BASE_URL}/${id}`);
};