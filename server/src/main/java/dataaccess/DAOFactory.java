package dataaccess;

public interface DAOFactory {

  AuthDAO getAuthDAO();
  UserDAO getUserDAO();
  GameDAO getGameDAO();
}
