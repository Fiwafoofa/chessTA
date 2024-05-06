package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

  GameData getGameData(Integer gameID) throws DataAccessException;

  Integer createGame(String gameName) throws DataAccessException;

  Collection<GameData> getAllGames() throws DataAccessException;

  void updateGameData(GameData gameData) throws DataAccessException;

  void clear() throws DataAccessException;
}
