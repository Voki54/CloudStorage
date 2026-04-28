import { createContext, useContext, useState } from "react";
import type { FileMetadata } from "../../types/file";
import type { ReactNode } from "react";
import { getFileDetails } from "../../api/fileApi";
import type { FileDetails } from "../../types/file";

interface FileDetailsContextType {
  selectedFile: FileDetails | null;
  loading: boolean;
  openFileDetails: (file: FileMetadata) => Promise<void>;
  closeFileDetails: () => void;
}

const FileDetailsContext = createContext<FileDetailsContextType | null>(null);

export function FileDetailsProvider({ children }: { children: ReactNode }) {
  const [selectedFile, setSelectedFile] = useState<FileDetails | null>(null);
  const [loading, setLoading] = useState(false);

  const openFileDetails = async (file: FileMetadata) => {
    setLoading(true);

    try {
      const details = await getFileDetails(file.id);
      setSelectedFile(details);
    } catch (e) {
      console.error("Failed to load file details", e);
    } finally {
      setLoading(false);
    }
  };

  const closeFileDetails = () => {
    setSelectedFile(null);
  };

  return (
    <FileDetailsContext.Provider
      value={{ selectedFile, loading, openFileDetails, closeFileDetails }}
    >
      {children}
    </FileDetailsContext.Provider>
  );
}

export function useFileDetails() {
  const ctx = useContext(FileDetailsContext);
  if (!ctx) throw new Error("useFileDetails must be used within provider");
  return ctx;
}