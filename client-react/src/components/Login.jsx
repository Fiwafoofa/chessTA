import { useState } from "react";
import { ServerFacade } from "../net/ServerFacade";

export const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");

    const loginUser = (e) => {
        e.preventDefault();
        new ServerFacade().loginUser(username, password);
    }

    const registerUser = (e) => {
        e.preventDefault();
        new ServerFacade().registerUser(username, password, email);
    }

    return (
        <>
            <h1>Login</h1>

            <form id="loginForm">
                <input type="text" placeholder="username" onChange={(e) => setUsername(e.target.value)}/>
                <input type="password" placeholder="password" onChange={(e) => setPassword(e.target.value)}/>
                <button type="submit" onClick={loginUser}>Login</button>
            </form>

            <form id="registerForm">
                <input type="text" placeholder="username" onChange={(e) => setUsername(e.target.value)}/>
                <input type="password" placeholder="password" onChange={(e) => setPassword(e.target.value)}/>
                <input type="text" placeholder="email" onChange={(e) => setEmail(e.target.value)}/>
                <button type="submit" onClick={registerUser}>Register</button>
            </form>
        </>
    );
}