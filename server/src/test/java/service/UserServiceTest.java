package service;

import dataaccess.DAOFactory;
import dataaccess.memory.MemoryDAOFactory;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import response.UnauthorizedException;

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
  void registerPos() {
    UserData newUser = new UserData("isaih", "is", "cool");
    assertDoesNotThrow(() -> {
      AuthData authData = userService.register(newUser);
      assertNotNull(authData.authToken());
      assertEquals("isaih", authData.username());
    });
  }

  @Test 
  void registerNeg() {
    assertFalse(false);
  }

  @Test
  void loginPos() {
    registerPos();

    UserData loginInfo = new UserData("isaih", "is", null);
    assertDoesNotThrow(() -> {
      AuthData authData = userService.login(loginInfo);
      assertNotNull(authData.authToken());
      assertEquals("isaih", authData.username());
    });

  }

  @Test
  void loginNeg() {
    registerPos();

    UserData loginInfo = new UserData("isaih", "wrong password", null);
    assertThrows(UnauthorizedException.class, () -> {
      userService.login(loginInfo);
    });
  }

  @Test
  void logoutPos() {
    assertDoesNotThrow(() -> {
      AuthData authData = userService.register(new UserData("isaih", "is", "cool"));
      userService.logout(authData.authToken());
    });
  }

  @Test
  void logoutNeg() {
    assertThrows(UnauthorizedException.class, () -> {
      userService.logout("myRandomBananaToken");
    });
  }

}