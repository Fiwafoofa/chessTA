package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

  private final HashMap<String, UserData> userDatas = new HashMap<>();
  @Override
  public UserData getUserData(String username) throws DataAccessException {
    return userDatas.get(username);
  }

  @Override
  public void addUserData(UserData userData) throws DataAccessException {
    userDatas.put(userData.username(), userData);
  }

  @Override
  public void clear() throws DataAccessException {
    userDatas.clear();
  }
}
