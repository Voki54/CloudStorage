import { Plus } from "lucide-react";
import { useRef } from "react";
import { uploadFile } from "../../api/fileApi";

interface Props {
  onUploadSuccess: () => void;
}

export default function UploadButton({ onUploadSuccess }: Props) {
  const inputRef = useRef<HTMLInputElement | null>(null);

  const handleClick = () => {
    inputRef.current?.click();
  };

  const handleFileChange = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      await uploadFile(file);
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
        className="w-full mb-1 flex flex-row items-center p-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition text-lg"
      >
        <Plus className="mr-2 w-6 h-6" />
        Загрузить
      </button>
    </>
  );
}