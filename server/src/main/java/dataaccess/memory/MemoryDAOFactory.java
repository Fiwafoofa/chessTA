package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class MemoryDAOFactory implements DAOFactory {

  private final AuthDAO authDAO = new MemoryAuthDAO();
  private final UserDAO userDAO = new MemoryUserDAO();
  private final GameDAO gameDAO = new MemoryGameDAO();

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
