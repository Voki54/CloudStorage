import { useState } from "react";
import { createPortal } from "react-dom";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onCreate: (name: string) => Promise<void>;
}

export default function CreateDirectoryModal({isOpen, onClose, onCreate,}: Props) {
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async () => {
    if (!name.trim()) return;

    try {
      setLoading(true);
      await onCreate(name);
      setName("");
      onClose();
    } catch (e) {
      console.error("Create directory failed", e);
    } finally {
      setLoading(false);
    }
  };

  return createPortal(
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-4 w-80">
        <h2 className="text-lg font-semibold text-gray-600 text-center mb-3">Новая папка</h2>

        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Название папки"
          className="w-full bg-gray-100 px-3 py-2 rounded-2xl mb-4"
          required
        />

        <div className="flex flex-row justify-between">
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="h-8 w-30 bg-blue-500 text-white px-3 py-1 rounded-xl hover:bg-blue-700 transition-colors"
          >
            Создать
          </button>

          <button onClick={onClose} className="h-8 w-30 text-gray-600 rounded-xl hover:bg-gray-200 transition-colors">Отмена</button>
        </div>
      </div>
    </div>,
    document.body
  );
}