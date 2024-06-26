import './App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Login } from "./components/Login"
import { PostLogin } from "./components/PostLogin"
import { Gameplay } from "./components/Gameplay"

function App() {
  return (
  <Router>
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/postLogin" element={<PostLogin />} />
      <Route path="/gameplay" element={<Gameplay />} />
    </Routes>
  </Router>
  );
}

export default App;
