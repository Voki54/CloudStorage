import type { FileMetadata } from "../../types/file";
import { Download, Trash2, Ellipsis } from "lucide-react";
import { IconButton } from "../ui/IconButton";
import { useState, useRef, useEffect } from "react";
import DropdownMenuItem from "../ui/DropdownMenuItem";
import { FileIcon } from "./FileIcon";

interface Props {
  file: FileMetadata;
  onDownload: (id: string) => void;
  onDelete: (id: string) => void;
  onOpenDetails: () => void;
}

function formatFileName(name: string, maxLength: number = 19): string {
  if (name.length < maxLength) return name;
  return `${name.slice(0, maxLength - 3)}...`;
}

export default function FileCard({ file, onDownload, onDelete, onOpenDetails }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);


  return (
    //overflow-hidden 
    <div className="group rounded-2xl hover:shadow-lg transition-all duration-300 relative w-40 flex flex-col" title={file.originalName}>
      <div className="w-full h-40 rounded-2xl flex items-center justify-center bg-gray-200">
        <FileIcon fileName={file.originalName} contentType={file.contentType} className="w-15 h-15 text-gray-500"></FileIcon>
        {/* <FileText className="w-15 h-15 text-gray-500" /> */}
      </div>

      <div className="h-12 relative flex items-center">
        <div className="flex-1 transition-all duration-200 group-hover:opacity-0 group-hover:pointer-events-none">
          <p className="font-medium text-gray-500 truncate text-center text-sm">
            {formatFileName(file.originalName)}
          </p>
        </div>

        <div className="absolute inset-0 flex items-center justify-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-200">
          <IconButton icon={Download} onClick={() => onDownload(file.id)}></IconButton>
          <IconButton icon={Trash2} onClick={() => onDelete(file.id)} variant="danger"></IconButton>
          <IconButton icon={Ellipsis} onClick={() => setIsOpen((prev) => !prev)}></IconButton>
        </div>
      </div>

      {isOpen && (
        <div
          ref={menuRef}
          className="absolute right-4 top-4 w-45 bg-white border border-gray-100 rounded-2xl shadow-lg z-10 py-3.5"
        >
          <DropdownMenuItem label="Открыть" />
          <DropdownMenuItem label="Переименовать" />
          <DropdownMenuItem label="Переместить" />
          <DropdownMenuItem label="Поделиться" />
          <DropdownMenuItem
            label="Сведения"
            onClick={onOpenDetails}
          />
        </div>
      )}
    </div>
  );
}