package service;

import dataaccess.DAOFactory;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryDAOFactory;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  DAOFactory daoFactory = new MemoryDAOFactory();
  UserService userService = new UserService(daoFactory);
  ClearService clearService = new ClearService(daoFactory);

  @BeforeEach
  void setup() {
    assertDoesNotThrow(() -> clearService.clear());
  }

  @Test
  void register() {
    UserData newUser = new UserData("isaih", "is", "cool");
    assertDoesNotThrow(() -> {
      AuthData authData = userService.register(newUser);
      assertNotNull(authData.authToken());
      assertEquals("isaih", authData.username());
    });
  }

  @Test
  void login() {
    register();

    UserData loginInfo = new UserData("isaih", "is", null);
    assertDoesNotThrow(() -> {
      AuthData authData = userService.login(loginInfo);
      assertNotNull(authData.authToken());
      assertEquals("isaih", authData.username());
    });

  }

  @Test
  void logout() {

  }

}