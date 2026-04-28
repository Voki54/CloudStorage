import { NavLink } from "react-router-dom";
import { HardDrive, Image, Star, UsersRound, HandHelping, ListChecks, Trash2, } from "lucide-react";
import UploadButton from "../ui/UploadButton";
import { useFiles } from "../context/FilesContext";

const items = [
  { label: "Все файлы", to: "/files", icon: HardDrive },
  { label: "Галерея", to: "/files/gallery", icon: Image },
  { label: "Избранное", to: "/files/favorites", icon: Star },
  { label: "Совместные", to: "/files/shared", icon: UsersRound },
  { label: "Запросы", to: "/files/requests", icon: HandHelping },
  { label: "Правила", to: "/files/rules", icon: ListChecks },
  { label: "Корзина", to: "/files/trash", icon: Trash2 },
];

export default function FileMenu() {
  const { loadFiles } = useFiles();

  return (
    <div className="flex flex-col gap-2">
      <UploadButton onUploadSuccess={loadFiles} />

      <nav className="flex flex-col gap-2">
        {items.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === "/files"}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2 rounded-xl transition ${isActive
                  ? "bg-blue-100 text-blue-600"
                  : "text-gray-600 hover:bg-gray-200"
                }`
              }
            >
              <Icon className="w-4 h-4" />
              {item.label}
            </NavLink>
          );
        })}
      </nav>
    </div>
  )
}

