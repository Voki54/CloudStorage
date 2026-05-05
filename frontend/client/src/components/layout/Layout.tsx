import Sidebar from "./Sidebar";
import SecondarySidebar from "./SecondarySidebar";
import Header from "./Header";
import DetailsSidebar from "./DetailsSidebar";
import type { ReactNode } from "react";
import { FilesProvider } from "../context/FilesContext";
import { FileDetailsProvider } from "../context/DetailsContext";
import { DirectoryProvider } from "../context/DirectoryContext";
import TopBar from "./TopBar";

export default function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="flex">
      <Sidebar />
      <DirectoryProvider>
        <FilesProvider>
          <SecondarySidebar />

          <div className="ml-65 flex-1 flex flex-col min-h-screen">
            <Header />
            <FileDetailsProvider>
              <div className="flex-1 flex flex-row">
                <div className="flex-1 flex flex-col">
                  <TopBar />

                  <main className="flex-1 bg-white">
                    {children}
                  </main>
                </div>

                <DetailsSidebar />
              </div>
            </FileDetailsProvider>
          </div>
        </FilesProvider>
      </DirectoryProvider>
    </div>
  );
}