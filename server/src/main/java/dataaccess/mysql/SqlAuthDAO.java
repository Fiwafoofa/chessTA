package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {
  @Override
  public AuthData getAuthData(String authToken) throws DataAccessException {
    String selectSql = "SELECT * FROM auth WHERE token = ?;";
    SqlCommand<AuthData> sqlCommand = (preparedStatement) -> {
      ResultSet resultSet = preparedStatement.executeQuery();
      return resultSet.next() ? new AuthData(
          resultSet.getString(1),
          resultSet.getString(2)
      ) : null;
    };
    return executeSqlStatement(sqlCommand, selectSql, authToken);
  }

  @Override
  public void addAuthData(AuthData authData) throws DataAccessException {
    String sql = "INSERT INTO auth VALUES (?, ?);";
    SqlCommand<?> sqlCommand = (preparedStatement) -> {
      preparedStatement.executeUpdate();
      return null;
    };
    executeSqlStatement(sqlCommand, sql, authData.authToken(), authData.username());
  }

  @Override
  public void deleteAuthData(String authToken) throws DataAccessException {
    String sql = "DELETE FROM auth WHERE token = ?;";
    SqlCommand<?> sqlCommand = (preparedStatement) -> {
      preparedStatement.executeUpdate();
      return null;
    };
    executeSqlStatement(sqlCommand, sql, authToken);
  }

  @Override
  public void clear() throws DataAccessException {
    SqlCommand<?> sqlCommand = PreparedStatement::executeUpdate;
    executeSqlStatement(sqlCommand, "DELETE FROM auth;");
  }
}
