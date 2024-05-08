package service;

import response.AlreadyTakenException;
import response.BadRequestException;
import response.UnauthorizedException;
import dataaccess.DAOFactory;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

  private final DAOFactory daoFactory;

  public UserService(DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  public AuthData register(UserData userData) throws Exception {
    if (userData.username() == null || userData.password() == null || userData.email() == null) {
      throw new BadRequestException();
    }
    if (daoFactory.getUserDAO().getUserData(userData.username()) != null) {
      throw new AlreadyTakenException();
    }
    String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
    UserData hashedUserData = new UserData(userData.username(), hashedPassword, userData.email());
    daoFactory.getUserDAO().addUserData(hashedUserData);

    AuthData authData = AuthData.generateAuthData(userData.username());
    daoFactory.getAuthDAO().addAuthData(authData);
    return authData;
  }

  public AuthData login(UserData userData) throws Exception {
    UserData dbUserData = daoFactory.getUserDAO().getUserData(userData.username());
    if (dbUserData == null
        || !userData.username().equals(dbUserData.username())
        || !BCrypt.checkpw(userData.password(), dbUserData.password())) {
      throw new UnauthorizedException();
    }
    AuthData authData = AuthData.generateAuthData(userData.username());
    daoFactory.getAuthDAO().addAuthData(authData);
    return authData;
  }

  public void logout(String authToken) throws Exception {
    if (daoFactory.getAuthDAO().getAuthData(authToken) == null) {
      throw new UnauthorizedException();
    }
    daoFactory.getAuthDAO().deleteAuthData(authToken);
  }
}
