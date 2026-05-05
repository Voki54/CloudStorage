import { useFiles } from "../components/context/FilesContext";
import { useDirectories } from "../components/context/DirectoryContext";
import type { ObjectItem } from "../types/object";
import ObjectList from "../components/ui/ObjectList";
import { deleteFile, downloadFile } from "../api/fileApi";
import { useFileDetails } from "../components/context/DetailsContext";

export default function ObjectPage() {
  const { files, loadFiles } = useFiles();
  const { directories, openDirectory } = useDirectories();
  const { openFileDetails } = useFileDetails();

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

  const handleDownload = async (item: ObjectItem) => {
    if (item.type === "file") {
      try {
        const res = await downloadFile(item.data.id);

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
    }
  };

  const handleDelete = async (item: ObjectItem) => {
    // if (!confirm("Delete this file?")) return;

    if (item.type === "file") {
      try {
        await deleteFile(item.data.id);
        loadFiles();
      } catch (e) {
        console.error("Delete failed", e);
        // alert("Failed to delete file");
      }
    }
  };

  const handleOpen = async (item: ObjectItem) => {
    if (item.type === "dir") {
      try {
        await openDirectory(item.data);
      } catch (e) {
        console.error("Open directory failed", e)
      }
    }
  }

  const handleOpenDetails = async (item: ObjectItem) => {
    if (item.type === "file") {
      try {
        await openFileDetails(item.data);
      } catch (e) {
        console.error("Open file details failed", e)
      }
    }
  }



  const items: ObjectItem[] = [
    ...directories.map(dir => ({ type: 'dir' as const, data: dir })),
    ...files.map(file => ({ type: 'file' as const, data: file }))
  ];

  return (
    <div className="p-8">
      <ObjectList
        items={items}
        onOpen={handleOpen}
        onDownload={handleDownload}
        onDelete={handleDelete}
        onOpenDetails={handleOpenDetails}
      />
    </div>
  );
}