import { useLocation } from "react-router-dom";
import FileMenu from "../files/FileMenu";

interface RouteConfig {
  title: string;
  menu: React.ReactElement;
}

const routesConfig: Record<string, RouteConfig> = {
  "files": {title: "Главная", menu: <FileMenu />},
  "directories": {title: "Директории", menu: <></>},
  "settings": {title: "Настройки", menu: <></>},
  "help": {title: "Справка", menu: <></>},
}

export default function SecondarySidebar() {
  const location = useLocation();
  const routeConfig = routesConfig[location.pathname.split('/')[1]] || "Главная";

  return (
    //bg-white
    <div className="fixed top-0 left-15 w-55 h-screen bg-gray-100 p-3 flex flex-col">

      <div className="h-10 mb-8 flex flex-col justify-evenly items-start ">
        <h1 className="text-lg font-semibold text-gray-600">
          {routeConfig.title}
        </h1>
      </div>

      {routeConfig.menu}
    </div>
  );
}