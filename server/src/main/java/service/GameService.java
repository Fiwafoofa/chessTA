package service;

import chess.ChessGame;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.*;
import dataaccess.DAOFactory;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class GameService {

  private final DAOFactory daoFactory;

  public GameService(DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  public ListGamesResponse listGames(String authToken) throws Exception {
    if (daoFactory.getAuthDAO().getAuthData(authToken) == null) {
      throw new UnauthorizedException();
    }

    Collection<GameData> games = daoFactory.getGameDAO().getAllGames();
    Collection<GameData> gamesWithoutChess = new HashSet<>();
    for (GameData game : games) {
      gamesWithoutChess.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
    }
    return new ListGamesResponse(gamesWithoutChess);
  }

  public CreateGameResponse createGame(CreateGameRequest createGameRequest, String authToken) throws Exception {
    if (daoFactory.getAuthDAO().getAuthData(authToken) == null) {
      throw new UnauthorizedException();
    }
    if (createGameRequest.gameName() == null) {
      throw new BadRequestException();
    }
    Integer gameID = daoFactory.getGameDAO().createGame(createGameRequest.gameName());
    return new CreateGameResponse(gameID);
  }

  public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws Exception {
    if (daoFactory.getAuthDAO().getAuthData(authToken) == null) {
      throw new UnauthorizedException();
    }
    if (joinGameRequest.gameID() == null || joinGameRequest.playerColor() == null) {
      throw new BadRequestException();
    }
    GameData dbGameData = daoFactory.getGameDAO().getGameData(joinGameRequest.gameID());
    if (dbGameData == null) {
      throw new BadRequestException("Invalid game id: " + joinGameRequest.gameID());
    }
    AuthData userAuthData = daoFactory.getAuthDAO().getAuthData(authToken);
    if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
      if (dbGameData.whiteUsername() == null) {
        daoFactory.getGameDAO().updateGameData(
          new GameData(
            dbGameData.gameID(),
            userAuthData.username(),
            dbGameData.blackUsername(),
            dbGameData.gameName(),
            dbGameData.game()
          )
        );
      } else if (!dbGameData.whiteUsername().equals(userAuthData.username())) {
        throw new AlreadyTakenException();
      }
      // user rejoining
    } else if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK) {
      if (dbGameData.blackUsername() == null) {
        daoFactory.getGameDAO().updateGameData(
          new GameData(
            dbGameData.gameID(),
            dbGameData.whiteUsername(),
            userAuthData.username(),
            dbGameData.gameName(),
            dbGameData.game()
          )
        );
      } else if (!dbGameData.blackUsername().equals(userAuthData.username())) {
        throw new AlreadyTakenException();
      }
      // user rejoining
    }
  }
}
