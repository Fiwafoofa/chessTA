import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ServerFacadeContext } from '../context/ServerFacadeContext';
import { GamesList } from './GamesList';

export const PostLogin = () => {

  const [gameId, setGameId] = useState();
  const [selectedColor, setSelectedColor] = useState();
  const [gameName, setGameName] = useState();
  const [gamesList, setGameList] = useState(null);
  const [errorMessage, setErrorMessage] = useState();
  const serverFacade = useContext(ServerFacadeContext);
  const navigate = useNavigate();

  const handleCreateGame = async (e) => {
    e.preventDefault();
    try {
      await serverFacade.doCreateGame(gameName);
    } catch (e) {
      setErrorMessage(e.message);
    }
  };

  const handleListGame = async () => {
    try {
      const gamesListRes = await serverFacade.doListGames();
      setGameList(gamesListRes);
    } catch (e) {
      setErrorMessage(e.message);
    }
  };

  const handleJoinGame = async (e) => {
    e.preventDefault();
    try {
      await serverFacade.doJoinGame(gameId, selectedColor);
    } catch (e) {
      setErrorMessage(e.message);
    }
  };

  const handleObserveGame = async (e) => {
    e.preventDefault();
    try {
      await serverFacade.doObserveGame(gameId);
    } catch (e) {
      setErrorMessage(e.message);
    }
  };

  const handleLogout = async () => {
    try {
      await serverFacade.doLogout();
      navigate('/');
    } catch (e) {
      alert(e.message);
      navigate('/');
    }
  };

  return (
    <div>
      <h1>PostLogin</h1>

      <form>
        <input type='text' placeholder='Game Name' onChange={(e) => setGameName(e.target.value)}></input>
        <button type='submit' onClick={(e) => handleCreateGame(e)}>Create Game</button>
      </form>

      <div>
        <button onClick={() => handleListGame()}>List Games</button>
        {gamesList !== null ? (
          <GamesList games={gamesList} />
        ) : (
          <br />
        )}
      </div>
      
      <form>
        <input type='text' placeholder='Game ID' onChange={(e) => setGameId(e.target.value)}></input>
        <input id='whiteColor' type='radio' name='color' value="WHITE" onChange={e => setSelectedColor(e.target.value)}></input>
        <label htmlFor="whiteColor">White</label>
        <input id='blackColor' type='radio' name='color' value="BLACK" onChange={e => setSelectedColor(e.target.value)}></input>
        <label htmlFor="blackColor">Black</label>
        <button type='submit' onClick={e => handleJoinGame(e)}>Join Game</button>
      </form>

      <form>
        <input type='text' placeholder='Game ID' onChange={e => setGameId(e.target.value)}></input>
        <button type='submit' onClick={e => handleObserveGame(e)}>Observe Game</button>
      </form>

      <div>
        <button onClick={() => handleLogout()}>Logout</button>
      </div>

      <p style={{color: "red"}}>{errorMessage}</p>

    </div>
  )
}