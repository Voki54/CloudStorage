import { Home, Folder, Settings, HelpCircle } from "lucide-react";
import { NavLink } from "react-router-dom";

const navItems = [
  { to: "/files", icon: Home },
  { to: "/directories", icon: Folder },
  { to: "/settings", icon: Settings },
  { to: "/help", icon: HelpCircle },
];

export default function Sidebar() {
  return (
    <div className="fixed top-0 left-0 w-15 h-screen bg-gray-100 flex flex-col items-center py-3">
      
      <div className="mb-22">
        <div className="w-10 h-10 flex items-center justify-center text-gray-700 text-4xl font-light bg-gray-300 rounded-3xl">
          <div className="-translate-y-0.5">
            Ф
          </div>
        </div>
      </div>

      <nav className="flex flex-col gap-2">
        {navItems.map((item, index) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={index}
              to={item.to}
              className={({ isActive }) =>
                `p-3 rounded-2xl transition ${
                  isActive
                    ? "bg-blue-100 text-blue-600"
                    : "text-gray-500 hover:bg-gray-200"
                }`
              }
            >
              <Icon className="w-5 h-5" />
            </NavLink>
          );
        })}
      </nav>
    </div>
  );
}