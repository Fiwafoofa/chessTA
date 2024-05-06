package dataaccess;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public interface DAOFactory {

  AuthDAO getAuthDAO();
  UserDAO getUserDAO();
  GameDAO getGameDAO();
}
