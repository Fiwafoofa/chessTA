package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

  private final HashMap<String, AuthData> authDatas = new HashMap<>();
  @Override
  public AuthData getAuthData(String authToken) throws DataAccessException {
    return authDatas.get(authToken);
  }

  @Override
  public void addAuthData(AuthData authData) throws DataAccessException {
    authDatas.put(authData.authToken(), authData);
  }

  @Override
  public void deleteAuthData(String authToken) throws DataAccessException {
    authDatas.remove(authToken);
  }

  @Override
  public void clear() throws DataAccessException {
    authDatas.clear();
  }
}
