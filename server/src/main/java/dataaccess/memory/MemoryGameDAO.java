package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

  private final HashMap<Integer, GameData> gameDatas = new HashMap<>();
  private Integer gameIDCounter = 1;

  @Override
  public GameData getGameData(Integer gameID) throws DataAccessException {
    return gameDatas.get(gameID);
  }

  @Override
  public Integer createGame(String gameName) throws DataAccessException {
    GameData newGame = new GameData(gameIDCounter, null, null, gameName, new ChessGame());
    gameDatas.put(gameIDCounter, newGame);
    return gameIDCounter++;
  }

  @Override
  public Collection<GameData> getAllGames() throws DataAccessException {
    return gameDatas.values();
  }

  @Override
  public void updateGameData(GameData gameData) throws DataAccessException {
    gameDatas.put(gameData.gameID(), gameData);
  }

  @Override
  public void clear() throws DataAccessException {
    gameDatas.clear();
  }
}
