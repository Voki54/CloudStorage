import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { getRoot, getDirectoryChildren } from '../../api/directoryApi';
import type { DirectoryDto } from '../../types/directory';

interface DirectoryContextType {
  currentDirectory?: DirectoryDto;
  directories: DirectoryDto[];
  history: DirectoryDto[];
  refreshDirectories: () => Promise<void>;
  openDirectory: (directory: DirectoryDto) => void;
  goToRoot: () => Promise<void>;
  goBack: () => void;
}

const DirectoryContext = createContext<DirectoryContextType | undefined>(undefined);

export function DirectoryProvider({ children }: { children: ReactNode }) {
  const [directories, setDirectories] = useState<DirectoryDto[]>([]);
  const [currentDirectory, setCurrentDirectory] = useState<DirectoryDto | undefined>(undefined);
  const [history, setHistory] = useState<DirectoryDto[]>([]);

  const openDirectory = (directory: DirectoryDto) => {
    if (!currentDirectory) return;
    setHistory(prev => [...prev, currentDirectory]);
    setCurrentDirectory(directory);
  };

  const goBack = () => {
    setHistory(prev => {
      const newHistory = [...prev];
      const last = newHistory.pop();

      if (last) {
        setCurrentDirectory(last);
      }

      return newHistory;
    });
  };

  const goToRoot = async () => {
    const root = await getRoot();
    setHistory([]);
    setCurrentDirectory(root);
  };

  const refreshDirectories = async () => {
    if (!currentDirectory) return;
    const data = await getDirectoryChildren(currentDirectory.id);
    setDirectories(data);
  };

  useEffect(() => {
    if (!currentDirectory) return;

    const dirId = currentDirectory.id;

    async function load() {
      const data = await getDirectoryChildren(dirId);
      setDirectories(data);
    }

    load();
  }, [currentDirectory]);

  useEffect(() => {
    async function init() {
      const root = await getRoot();
      setCurrentDirectory(root);
    }

    init();
  }, []);

  return (
    <DirectoryContext.Provider
      value={{
        currentDirectory,
        directories,
        history,
        refreshDirectories,
        openDirectory,
        goToRoot,
        goBack
      }}
    >
      {children}
    </DirectoryContext.Provider>
  );
}

export function useDirectories() {
  const context = useContext(DirectoryContext);
  if (!context) {
    throw new Error('useDirectories must be used within DirectoryProvider');
  }
  return context;
}