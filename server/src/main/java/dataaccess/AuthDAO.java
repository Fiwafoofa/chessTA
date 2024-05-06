package dataaccess;

import model.AuthData;

public interface AuthDAO {

  AuthData getAuthData(String authToken) throws DataAccessException;

  void addAuthData(AuthData authData) throws DataAccessException;

  void deleteAuthData(String authToken) throws DataAccessException;

  void clear() throws DataAccessException;
}
