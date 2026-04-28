export interface FileMetadata {
  id: string;
  originalName: string;
  size: number;
  contentType: string;
}

export interface UploadResponse {
  fileId: string;
}

export interface FileDetails {
  id: string;
  originalName: string;
  size: number;
  contentType: string;
  createdAt: string;
  updatedAt: string;
}