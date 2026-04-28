import { Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/layout/Layout";
import FilesPage from "./pages/FilesPage";
import DirectoriesPage from "./pages/DirectoriesPage";
import SettingsPage from "./pages/SettingsPage";
import HelpPage from "./pages/HelpPage";
// import keycloak from "./keycloak";

export default function App() {
  return (
    <Layout>
      {/* <pre>{keycloak.token}</pre> */}
      <Routes>
        <Route path="/" element={<Navigate to="/files" replace />} />
        <Route path="/files" element={<FilesPage />} />
        <Route path="/directories" element={<DirectoriesPage />} />
        <Route path="/settings" element={<SettingsPage />} />
        <Route path="/help" element={<HelpPage />} />
      </Routes>
    </Layout>
  );
}


// import FilesPage from "./pages/FilesPage";

// function App() {
//   const handleLogout = () => {
//     keycloak.logout({ redirectUri: 'http://localhost:3000' });
//   };

//   return (
    
//     <div>


//       {/* <h1>Logged in</h1>
//       <pre>{keycloak.token}</pre> */}
//       <FilesPage />

//       <button
//         onClick={handleLogout}
//         style={{
//           padding: '10px 20px',
//           backgroundColor: '#dc3545',
//           color: 'white',
//           border: 'none',
//           borderRadius: '5px',
//           cursor: 'pointer',
//           fontSize: '16px'
//         }}
//       >
//         Logout
//       </button>
//     </div>
//   );
// }

// export default App;