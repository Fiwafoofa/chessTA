package dataaccess.mysql;

import dataaccess.*;

public class SqlDAOFactory implements DAOFactory {

  private final AuthDAO authDAO;
  private final UserDAO userDAO;
  private final GameDAO gameDAO;

  public SqlDAOFactory() {
    try {
      authDAO = new SqlAuthDAO();
      userDAO = new SqlUserDAO();
      gameDAO = new SqlGameDAO();
    } catch (DataAccessException dataAccessException) {
      throw new RuntimeException(dataAccessException.getMessage());
    }
  }

  @Override
  public AuthDAO getAuthDAO() {
    return authDAO;
  }

  @Override
  public UserDAO getUserDAO() {
    return userDAO;
  }

  @Override
  public GameDAO getGameDAO() {
    return gameDAO;
  }
}
