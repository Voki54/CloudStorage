import api from "./axios";

const BASE_URL  = "/api/files"

export const getFiles = async () => {
  const res = await api.get(BASE_URL);
  return res.data;
};

export const getFileDetails = async (id: string) => {
  const res = await api.get(`${BASE_URL}/${id}`);
  return res.data;
};

export const uploadFile = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const res = await api.post(BASE_URL, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return res.data;
};

export const downloadFile = async (id: string) => {
  const res = await api.get(`${BASE_URL}/${id}/download`, {
    responseType: "blob",
  });

  return res;
};

export const deleteFile = async (id: string) => {
  await api.delete(`${BASE_URL}/${id}`);
};