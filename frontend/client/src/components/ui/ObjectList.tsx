import type { ObjectItem } from "../../types/object";
import { BaseCard } from "./BaseCard";

interface Props {
  items: ObjectItem[];
  onOpen: (item: ObjectItem) => Promise<void>;
  onDownload: (item: ObjectItem) => Promise<void>;
  onDelete: (item: ObjectItem) => Promise<void>;
  onOpenDetails: (item: ObjectItem) => Promise<void>;
}

export default function ObjectList({ items, onOpen, onDownload, onDelete, onOpenDetails }: Props) {
  if (items.length === 0) {
    return <p className="text-gray-500">No files</p>;
  }

  return (
    <div className="grid grid-cols-[repeat(auto-fill,minmax(160px,1fr))] gap-4 justify-center justify-items-center">
      {items.map((item) => (
        <BaseCard
          key={item.data.id}
          item={item}
          onOpen={onOpen}
          onDownload={onDownload}
          onDelete={onDelete}
          onOpenDetails={onOpenDetails}
        />
      ))}
    </div>
  );
}