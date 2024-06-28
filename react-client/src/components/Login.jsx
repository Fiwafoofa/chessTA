import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ServerFacadeContext } from '../context/ServerFacadeContext';

export const Login = () => {
  
  const [username, setUsername] = useState();
  const [password, setPassword] = useState();
  const [email, setEmail] = useState();
  const [errorMessage, setErrorMessage] = useState("");
  const serverFacade = useContext(ServerFacadeContext);
  const navigate = useNavigate();
  
  async function handleLogin(e) {
    e.preventDefault();
    try {
      await serverFacade.doLogin(username, password);
      navigate("/postLogin");
    } catch (e) {
      setErrorMessage(e.message);
    }
    
  }

  async function handleRegister(e) {
    e.preventDefault();
    try {
      await serverFacade.doRegister(username, password, email);
      navigate("/postLogin");
    } catch (e) {
      setErrorMessage(e.message);
    }
    
  }

  return (
    <div>
      <h1>Login</h1>

      <form>
        <input type="text" placeholder='username' onChange={(e) => setUsername(e.target.value)}></input>
        <input type="password" placeholder='password' onChange={(e) => setPassword(e.target.value)}></input>
        <button type='submit' onClick={(e) => handleLogin(e)}>Login</button>
      </form>

      <form>
        <input type="text" placeholder='username' onChange={(e) => setUsername(e.target.value)}></input>
        <input type="password" placeholder='password' onChange={(e) => setPassword(e.target.value)}></input>
        <input type="email" placeholder='email' onChange={(e) => setEmail(e.target.value)}></input>
        <button type='submit' onClick={(e) => handleRegister(e)}>Register</button>
      </form>

      <h3 style={{color: "red"}}>{errorMessage}</h3>
    </div>
  )
}