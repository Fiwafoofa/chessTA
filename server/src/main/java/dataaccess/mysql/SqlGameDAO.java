package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class SqlGameDAO extends SqlDAO implements GameDAO {
  @Override
  public GameData getGameData(Integer gameID) throws DataAccessException {
    String sql = "SELECT * FROM game WHERE gameID = ?;";
    SqlCommand<GameData> sqlCommand = preparedStatement -> {
      ResultSet resultSet = preparedStatement.executeQuery();
      return resultSet.next() ? readGameData(resultSet) : null;
    };
    return executeSqlStatement(sqlCommand, sql, gameID);
  }

  @Override
  public Integer createGame(String gameName) throws DataAccessException {
    String sql = "INSERT INTO game (gameName, chessGame) VALUES (?, ?);";
    SqlCommand<Integer> sqlCommand = preparedStatement -> {
      preparedStatement.executeUpdate();
      ResultSet resultSetKeys = preparedStatement.getGeneratedKeys();
      return resultSetKeys.next() ? resultSetKeys.getInt(1) : -1;

    };
    return executeSqlStatement(sqlCommand, sql, gameName, new ChessGame());
  }

  @Override
  public Collection<GameData> getAllGames() throws DataAccessException {
    String sql = "SELECT * FROM game;";
    SqlCommand<Collection<GameData>> sqlCommand = preparedStatement -> {
      ResultSet resultSet = preparedStatement.executeQuery();
      GameData gameData;
      Collection<GameData> gameDataCollection = new HashSet<>();
      while (resultSet.next()) {
        gameData = readGameData(resultSet);
        gameDataCollection.add(gameData);
      }
      return gameDataCollection;
    };
    return executeSqlStatement(sqlCommand, sql);
  }

  @Override
  public void updateGameData(GameData gameData) throws DataAccessException {
    String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, chessGame = ? WHERE gameID = ?";
    SqlCommand<?> sqlCommand = preparedStatement -> {
      preparedStatement.executeUpdate();
      return null;
    };
    executeSqlStatement(
        sqlCommand,
        sql,
        gameData.whiteUsername(),
        gameData.blackUsername(),
        gameData.game(),
        gameData.gameID()
    );
  }

  @Override
  public void clear() throws DataAccessException {
    SqlCommand<?> sqlCommand = PreparedStatement::executeUpdate;
    executeSqlStatement(sqlCommand, "DELETE FROM game");
  }

  private GameData readGameData(ResultSet rs) throws SQLException {
    Integer gameID = rs.getInt("gameID");
    String gameName = rs.getString("gameName");
    String whiteUsername = rs.getString("whiteUsername");
    String blackUsername = rs.getString("blackUsername");
    String chessGameJson = rs.getString("chessGame");
    ChessGame chessGame = serializer.fromJson(chessGameJson, ChessGame.class);
    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

  }
}
