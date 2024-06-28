import React from 'react';

export const GamesList = ({ games }) => {

  return (
    <div id='gamesList'>
      <ol>
        {games.map((game, index) => 
          <li key={index}>
            GameID: {game.gameID} <br />
            Game Name: {game.gameName}  <br />
            White Username: {game.whiteUsername} <br /> 
            Black Username: {game.blackUsername}
          </li>
        )}
      </ol>
    </div>
  );
}