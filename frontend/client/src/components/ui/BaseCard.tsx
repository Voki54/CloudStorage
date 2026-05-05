import { ObjectIcon } from "./ObjectIcon";
import { IconButton } from "./IconButton";
import type { ObjectItem } from "../../types/object";
import { Download, Trash2, Ellipsis } from "lucide-react";
import { useState, useRef, useEffect } from "react";
import DropdownMenuItem from "./DropdownMenuItem";

type BaseCardProps = {
  item: ObjectItem;
  onOpen: (item: ObjectItem) => Promise<void>;
  onDownload: (item: ObjectItem) => Promise<void>;
  onDelete: (item: ObjectItem) => Promise<void>;
  onOpenDetails: (item: ObjectItem) => Promise<void>;
}

function formatCardName(name: string, maxLength: number = 19): string {
  if (name.length < maxLength) return name;
  return `${name.slice(0, maxLength - 3)}...`;
}

export function BaseCard({ item, onOpen, onDownload, onDelete, onOpenDetails }: BaseCardProps) {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement | null>(null);
  let title;
  let contentType;

  if (item.type === "dir") {
    title = item.data.name;
  } else {
    title = item.data.originalName;
    contentType = item.data.contentType;
  }

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
    <div
      className="group rounded-2xl hover:shadow-lg transition-all duration-300 relative w-40 flex flex-col"
      title={title}
      onDoubleClick={() => onOpen(item)}
      >
      <div className="w-full h-40 rounded-2xl flex items-center justify-center bg-gray-200">
        <ObjectIcon objectName={title} contentType={contentType} type={item.type} className="w-15 h-15 text-gray-500"></ObjectIcon>
      </div>

      <div className="h-12 relative flex items-center">
        <div className="flex-1 transition-all duration-200 group-hover:opacity-0 group-hover:pointer-events-none">
          <p className="font-medium text-gray-500 truncate text-center text-sm">
            {formatCardName(title)}
          </p>
        </div>

        <div className="absolute inset-0 flex items-center justify-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-200">
          <IconButton icon={Download} onClick={() => onDownload(item)}></IconButton>
          <IconButton icon={Trash2} onClick={() => onDelete(item)} variant="danger"></IconButton>
          <IconButton icon={Ellipsis} onClick={() => setIsOpen((prev) => !prev)}></IconButton>
        </div>
      </div>

      {isOpen && (
        <div
          ref={menuRef}
          className="absolute right-4 top-4 w-45 bg-white border border-gray-100 rounded-2xl shadow-lg py-3.5"
        >
          <DropdownMenuItem label="Открыть" />
          <DropdownMenuItem label="Переименовать" />
          <DropdownMenuItem label="Переместить" />
          <DropdownMenuItem label="Поделиться" />
          <DropdownMenuItem
            label="Сведения"
            onClick={() => onOpenDetails(item)}
          />
        </div>
      )}
    </div>
  );
}