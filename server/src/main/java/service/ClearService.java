package service;

import dataaccess.DAOFactory;

public class ClearService {

  private final DAOFactory daoFactory;

  public ClearService(DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
  }
  public void clear() throws Exception {
    daoFactory.getAuthDAO().clear();
    daoFactory.getGameDAO().clear();
    daoFactory.getUserDAO().clear();
  }
}
