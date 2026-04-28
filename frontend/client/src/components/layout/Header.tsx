import { useLocation } from "react-router-dom";
import { Search, Bell, Moon, UserRound } from "lucide-react";
import { IconButton } from "../ui/IconButton";

export const routeTitles: Record<string, string> = {
  "/files": "Все файлы",
  "/files/gallery": "Галерея",
  "/files/favorites": "Избранное",
  "/files/shared": "Совместные",
  "/files/requests": "Запросы",
  "/files/rules": "Правила",
  "/files/trash": "Корзина",
};

export default function Header() {
  const location = useLocation();

  const title = routeTitles[location.pathname] || "";

  return (
    <div className="sticky top-0 z-15 w-full bg-white px-6 py-3 flex items-center justify-between">

      <div className="flex items-center gap-6">
        {title && (
          <h1 className="text-lg font-semibold text-gray-600">
            {title}
          </h1>
        )}

        <div className="flex items-center bg-gray-100 rounded-xl px-3 py-2 w-72">
          <Search className="w-4 h-4 text-gray-500 mr-2" />
          <input
            type="text"
            placeholder="Search..."
            className="bg-transparent outline-none text-sm w-full"
          />
        </div>
      </div>

      <div className="flex items-center gap-3">
        <IconButton icon={Moon} onClick={() => { }} size={5}></IconButton>
        <IconButton icon={Bell} onClick={() => { }} size={5}></IconButton>

        <div className="w-9 h-9 bg-gray-300 rounded-full flex items-center justify-center">
          <UserRound className="w-5 h-5 text-gray-600" />
        </div>
      </div>
    </div>
  );
}