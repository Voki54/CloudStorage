import { deleteFile, downloadFile } from "../api/fileApi";
import FileList from "../components/files/FileList";
import { useFiles } from "../components/context/FilesContext";

export default function FilesPage() {
  const { files, loadFiles } = useFiles();

  const getFileNameFromHeader = (contentDisposition: string | null | undefined): string => {
    const deafaultName = 'file';

    if (!contentDisposition) {
      return deafaultName;
    }

    // Поиск filename* (современный стандарт с UTF-8)
    const filenameStarMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/);
    if (filenameStarMatch && filenameStarMatch[1]) {
      return decodeURIComponent(filenameStarMatch[1]);
    }

    // Поиск обычного filename
    const filenameMatch = contentDisposition.match(/filename[^*]=["']?(.+?)["']?(?:;|$)/);
    if (filenameMatch && filenameMatch[1]) {
      return filenameMatch[1].replace(/["']/g, '');
    }

    return deafaultName;
  };

  const handleDownload = async (id: string) => {
    try {
      const res = await downloadFile(id);

      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");

      link.href = url;
      link.setAttribute("download", getFileNameFromHeader(res.headers["content-disposition"]));
      document.body.appendChild(link);
      link.click();

      setTimeout(() => {
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      }, 100);
    } catch (error) {
      console.error("Download failed:", error);
    }
  };

  const handleDelete = async (id: string) => {
    // if (!confirm("Delete this file?")) return;

    try {
      await deleteFile(id);
      loadFiles();
    } catch (e) {
      console.error("Delete failed", e);
      // alert("Failed to delete file");
    }
  };

  return (
    <div className="flex flex-col rounded-tl-3xl justify-items-start p-8">
      {/* <h1 className="text-2xl font-bold mb-6">My Files</h1> */}
      <FileList
        files={files}
        onDownload={handleDownload}
        onDelete={handleDelete}
      />
    </div>
  );
}