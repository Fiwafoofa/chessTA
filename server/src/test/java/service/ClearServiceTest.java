package service;

import dataaccess.DAOFactory;
import dataaccess.memory.MemoryDAOFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

  DAOFactory memoryDaoFactory = new MemoryDAOFactory();
  ClearService clearService = new ClearService(memoryDaoFactory);

  @Test
  void clear() {
    assertDoesNotThrow(() -> clearService.clear());
  }
}