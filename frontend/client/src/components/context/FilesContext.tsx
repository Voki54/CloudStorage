import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { FileMetadata } from '../../types/file';
import { getFilesByDirectory } from '../../api/fileApi';
import { useDirectories } from './DirectoryContext';

interface FilesContextType {
  files: FileMetadata[];
  loadFiles: () => Promise<void>;
}

const FilesContext = createContext<FilesContextType | undefined>(undefined);

export function FilesProvider({ children }: { children: ReactNode }) {
  const [files, setFiles] = useState<FileMetadata[]>([]);
  const { currentDirectory } = useDirectories();

  const loadFiles = async () => {
    if (!currentDirectory) return;

    const data = await getFilesByDirectory(currentDirectory.id);
    setFiles(data);
  };

  useEffect(() => {
    if (currentDirectory) {
      loadFiles();
    }
  }, [currentDirectory]);

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