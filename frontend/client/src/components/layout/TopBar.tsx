import { ListFilterPlus, ArrowLeft, LayoutGrid } from "lucide-react";
import { IconButton } from "../ui/IconButton";
import { useDirectories } from "../context/DirectoryContext";

export default function TopBar() {
  const { goBack, history, currentDirectory } = useDirectories();
  const lastHistoryItem = history.length > 0 ? history[history.length - 1] : null;
  return (
    <div className="sticky top-15 w-full h-14 bg-white flex items-center justify-between px-5 z-10">

      {lastHistoryItem ?
        <div className="flex items-center gap-2">
          <IconButton icon={ArrowLeft} onClick={goBack} size={5}></IconButton>
          <h2 className="text-lg font-semibold text-gray-600">{currentDirectory?.name}</h2>
        </div>
        : <div></div>
      }

      <div className="flex items-center gap-2">
        <IconButton icon={ListFilterPlus} onClick={() => { }} size={5}></IconButton>
        <IconButton icon={LayoutGrid} onClick={() => { }} size={5}></IconButton>
      </div>
    </div>
  );
}