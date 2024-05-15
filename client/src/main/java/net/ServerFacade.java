package net;

import java.util.Collection;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

public class ServerFacade {

  private final HttpCommunicator httpCommunicator;
  private final WebsocketCommunicator websocketCommunicator;
  private String token;

  public ServerFacade(String domainName, ServerMessageObserver serverMessageObserver) {
    httpCommunicator = new HttpCommunicator("http://" + domainName);
    websocketCommunicator = new WebsocketCommunicator("ws://" + domainName, serverMessageObserver);
    token = null;
  }

  public void register(String username, String password, String email) throws ResponseException {
    UserData userData = new UserData(username, password, email);
    AuthData authDataResponse = httpCommunicator.makeRequest("POST", "/user", userData, AuthData.class, token);
    token = authDataResponse.authToken();
  }

  public void login(String username, String password) throws ResponseException {
    UserData userData = new UserData(username, password, null);
    AuthData authDataResponse = httpCommunicator.makeRequest("POST", "/session", userData, AuthData.class, token);
    token = authDataResponse.authToken();
  }

  public void logout() throws ResponseException {
    httpCommunicator.makeRequest("DELETE", "/session", null, null, token);
    token = null;
  }

  public Collection<GameData> listGames() throws ResponseException {
    ListGamesResponse listGamesResponse = httpCommunicator.makeRequest(
      "GET", 
      "/game", 
      null, 
      ListGamesResponse.class, 
      token
    );
    
    return listGamesResponse.games;
  }

  public Integer createGame(String gameName) throws ResponseException {
    CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
    CreateGameResponse response = httpCommunicator.makeRequest(
      "POST", 
      "/game", 
      createGameRequest, 
      CreateGameResponse.class, 
      token
    );
    return response.getGameID();
  }

  public void joinGame(Integer gameID, String teamColor) throws ResponseException {
    JoinGameRequest joinGameRequest = new JoinGameRequest(teamColor, gameID);
    httpCommunicator.makeRequest("PUT", "/game", joinGameRequest, null, token);
  }

  public void joinObserver(Integer gameID) {
    System.out.println("ATTEMPTED TO OBSERVE");
  }
}
