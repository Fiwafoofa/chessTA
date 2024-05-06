package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.*;

import static java.sql.Types.NULL;

public class SqlDAO {

  protected Gson serializer = new Gson();

  protected interface SqlCommand<T> {
    T execute(PreparedStatement preparedStatement) throws SQLException;
  }

  protected <T> T executeSqlStatement(SqlCommand<T> sqlCommand, String sqlStatement, Object... args) throws DataAccessException {
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {
      fillPreparedStatement(preparedStatement, args);
      return sqlCommand.execute(preparedStatement);
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  protected void fillPreparedStatement(PreparedStatement preparedStatement, Object... args) throws SQLException {
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];

      // Iffy dependency. should be closed for modification, open to extension
      switch (arg) {
        case String p -> preparedStatement.setString(i + 1, p);
        case Integer p -> preparedStatement.setInt(i + 1, p);
        case ChessGame p -> preparedStatement.setString(i + 1, serializer.toJson(p));
        case null -> preparedStatement.setNull(i + 1, NULL);
        default -> {
        }
      }
    }
//    preparedStatement.executeUpdate();
//    ResultSet resultSet = preparedStatement.getGeneratedKeys();
//    return resultSet.next() ? resultSet.getInt(1) : 0;
  }

}
