package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlUserDAO extends SqlDAO implements UserDAO {

  public SqlUserDAO() throws DataAccessException {
    DatabaseManager.createDatabase();
  }

  @Override
  public UserData getUserData(String username) throws DataAccessException {
    String sqlSelect = "SELECT * FROM user WHERE username = ?;";
    SqlCommand<UserData> sqlCommand = (preparedStatement) -> {
      ResultSet resultSet = preparedStatement.executeQuery();
      return resultSet.next() ?
          new UserData(
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getString(3)
          ) : null;
    };
    return executeSqlStatement(sqlCommand, sqlSelect, username);
  }

  @Override
  public void addUserData(UserData userData) throws DataAccessException {
    String sqlInsert = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
    SqlCommand<?> sqlCommand = preparedStatement -> {
      preparedStatement.executeUpdate();
      return null;
    };
    executeSqlStatement(sqlCommand, sqlInsert, userData.username(), userData.password(), userData.email());
  }

  @Override
  public void clear() throws DataAccessException {
    String sql = "DELETE FROM user;";
    SqlCommand<?> sqlCommand = (PreparedStatement::executeUpdate);
    executeSqlStatement(sqlCommand, sql);
  }

}
