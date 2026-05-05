import { CloudUpload } from "lucide-react";
import { useRef } from "react";
import { uploadFile } from "../../api/fileApi";
import { useDirectories } from "../context/DirectoryContext";

interface Props {
  onUploadSuccess: () => void;
}

export default function UploadButton({ onUploadSuccess }: Props) {
  const { currentDirectory } = useDirectories();
  const inputRef = useRef<HTMLInputElement | null>(null);

  const handleClick = () => {
    inputRef.current?.click();
  };

  const handleFileChange = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!currentDirectory) {
      console.error("No directory selected");
      return;
    }

    try {
      await uploadFile(file, currentDirectory.id);
      onUploadSuccess();
    } catch (err) {
      console.error("Upload failed", err);
    }
  };

  return (
    <>
      <input
        type="file"
        ref={inputRef}
        onChange={handleFileChange}
        className="hidden"
      />

      <button
        onClick={handleClick}
        className="w-full mb-1 flex flex-row items-center px-3 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition text-lg"
      >
        <CloudUpload className="mr-2 w-5 h-5" />
        Загрузить
      </button>
    </>
  );
}