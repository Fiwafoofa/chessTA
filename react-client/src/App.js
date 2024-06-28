import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Login } from './components/Login';
import { PostLogin } from './components/PostLogin';
import { Gameplay } from './components/Gameplay';
import { useState } from 'react';
import { ServerFacade } from './net/ServerFacade';
import { ServerFacadeContext } from './context/ServerFacadeContext';

function App() {

  const [serverFacade] = useState(new ServerFacade());

  return (
    <ServerFacadeContext.Provider value={serverFacade}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="postLogin" element={<PostLogin />} />
          <Route path="gameplay" element={<Gameplay />} />
        </Routes>
      </BrowserRouter>
    </ServerFacadeContext.Provider>
    
  );
}

export default App;
