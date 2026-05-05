import { Plus } from "lucide-react";
import { useState } from "react";
import { createDirectory } from "../../api/directoryApi";
import CreateDirectoryModal from "../dirs/CreateDirectoryModal";
import { useDirectories } from "../context/DirectoryContext";

interface Props {
  onCreateSuccess: () => void;
}

export default function CreateButton({ onCreateSuccess }: Props) {
  const { currentDirectory } = useDirectories();
  const [open, setOpen] = useState(false);

  const handleCreate = async (name: string) => {
    if (!currentDirectory) {
      console.error("No directory selected");
      return;
    }

    await createDirectory(name, currentDirectory.id);

    onCreateSuccess();
  }

  return (
    <>
      <button
        onClick={() => setOpen(true)}
        className="w-full mb-1 flex flex-row items-center px-3 py-2 bg-blue-400 text-white rounded-xl hover:bg-blue-700 transition text-lg"
      >
        <Plus className="mr-2 w-5 h-5" />
        Новая папка
      </button>

      <CreateDirectoryModal
        isOpen={open}
        onClose={() => setOpen(false)}
        onCreate={handleCreate}
      />
    </>
  );
}