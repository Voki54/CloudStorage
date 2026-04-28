import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { FileMetadata } from '../../types/file';
import { getFiles } from '../../api/fileApi';

interface FilesContextType {
  files: FileMetadata[];
  loadFiles: () => Promise<void>;
}

const FilesContext = createContext<FilesContextType | undefined>(undefined);

export function FilesProvider({ children }: { children: ReactNode }) {
  const [files, setFiles] = useState<FileMetadata[]>([]);

  const loadFiles = async () => {
    const data = await getFiles();
    setFiles(data);
  };

  useEffect(() => {
    loadFiles();
  }, []);

  return (
    <FilesContext.Provider value={{ files, loadFiles }}>
      {children}
    </FilesContext.Provider>
  );
}

export function useFiles() {
  const context = useContext(FilesContext);
  if (!context) {
    throw new Error('useFiles must be used within FilesProvider');
  }
  return context;
}