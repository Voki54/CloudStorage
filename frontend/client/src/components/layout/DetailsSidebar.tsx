import { useFileDetails } from "../context/DetailsContext";
import { X } from "lucide-react";
import { IconButton } from "../ui/IconButton";
import { ObjectIcon } from "../ui/ObjectIcon";

function formatSize(size: number) {
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
  return (size / (1024 * 1024)).toFixed(2) + " MB";
}

export default function FileDetailsSidebar() {
  const { selectedFile, loading, closeFileDetails } = useFileDetails();

  if (!selectedFile && !loading) return null;

  return (
    <div className="w-70">
      <div className="fixed right-0 w-70 rounded-tl-3xl bg-gray-100 h-full p-2 flex flex-col">

        <div className="flex justify-between items-center mb-4">
          <h2 className="flex-1 text-lg font-semibold text-gray-600 text-center">Сведения</h2>
          <IconButton icon={X} onClick={closeFileDetails} size={5}></IconButton>
        </div>

        {loading && <p className="text-sm text-gray-600">Loading...</p>}

        {!loading && selectedFile && (
          <div className="px-3">
            <div className="h-30 flex justify-center items-center mb-4">
              <ObjectIcon
                objectName={selectedFile.originalName}
                contentType={selectedFile.contentType}
                type="file"
                className="w-15 h-15 text-gray-500"
              />
            </div>

            <div className="space-y-4 text-sm">

              <div>
                <p className="text-blue-600 text-[13px] font-bold">Имя</p>
                <p className="text-gray-600 font-bold break-all">
                  {selectedFile.originalName}
                </p>
              </div>

              <div>
                <p className="text-blue-600 text-[13px] font-bold">Размер</p>
                <p className="text-gray-600 font-bold">
                  {formatSize(selectedFile.size)}
                </p>
              </div>

              <div>
                <p className="text-blue-600 text-[13px] font-bold">Владелец</p>
                <p className="text-gray-600 font-bold break-all">
                  Вы
                </p>
              </div>

              <div>
                <p className="text-blue-600 text-[13px] font-bold">Создан</p>
                <p className="text-gray-600 font-bold">
                  {new Date(selectedFile.createdAt).toLocaleString()}
                </p>
              </div>

              <div>
                <p className="text-blue-600 text-[13px] font-bold">Обновлён</p>
                <p className="text-gray-600 font-bold">
                  {new Date(selectedFile.updatedAt).toLocaleString()}
                </p>
              </div>

            </div>
          </div>
        )}
      </div>
    </div>
  );
}