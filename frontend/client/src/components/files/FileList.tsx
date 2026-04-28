import FileCard from "./FileCard";
import type { FileMetadata } from "../../types/file";
import { useFileDetails } from "../context/DetailsContext";

interface Props {
  files: FileMetadata[];
  onDownload: (id: string) => void;
  onDelete: (id: string) => void;
}

export default function FileList({ files, onDownload, onDelete}: Props) {
  const { openFileDetails } = useFileDetails();
  
  if (files.length === 0) {
    return <p className="text-gray-500">No files</p>;
  }

  return (
    <div className="grid grid-cols-[repeat(auto-fill,minmax(160px,1fr))] gap-4 justify-center justify-items-center">
      {files.map((file) => (
        <FileCard
          key={file.id}
          file={file}
          onDownload={onDownload}
          onDelete={onDelete}
          onOpenDetails={() => openFileDetails(file)}
        />
      ))}
    </div>
  );
}