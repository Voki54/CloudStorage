import Sidebar from "./Sidebar";
import SecondarySidebar from "./SecondarySidebar";
import Header from "./Header";
import DetailsSidebar from "./DetailsSidebar";
import type { ReactNode } from "react";
import { FilesProvider } from "../context/FilesContext";
import { FileDetailsProvider } from "../context/DetailsContext";
// import { Outlet } from "react-router-dom";

export default function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="flex">
      <Sidebar />
      <FilesProvider>
        <SecondarySidebar />

        <div className="ml-70 flex-1 flex flex-col min-h-screen">
          <Header />
          <FileDetailsProvider>
            <div className="flex-1 flex flex-row">
              <main className="flex-1 bg-white">
                {/* <Outlet context={{ files, loadFiles }} /> */}
                {children}
              </main>
              
              <DetailsSidebar />
            </div>
          </FileDetailsProvider>

        </div>
      </FilesProvider>
    </div>
  );
}