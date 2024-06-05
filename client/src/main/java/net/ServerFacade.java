package net;

import java.util.Collection;

import chess.ChessMove;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

public class ServerFacade {

  private final HttpCommunicator httpCommunicator;
  private final ServerMessageObserver serverMessageObserver;
  private final String domainName;
  private WebsocketCommunicator websocketCommunicator = null;
  private String token;

  public ServerFacade(String domainName, ServerMessageObserver serverMessageObserver) {
    httpCommunicator = new HttpCommunicator("http://" + domainName);
    this.serverMessageObserver = serverMessageObserver;
    this.domainName = domainName;
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
    joinObserver(gameID);
  }

  public void joinObserver(Integer gameID) throws ResponseException {
    ConnectCommand connectCommand = new ConnectCommand(token, gameID);
    getWebsocketCommunicator().send(connectCommand);
    
  }

  public void leaveGame(Integer gameID) throws ResponseException {
    LeaveCommand leaveCommand = new LeaveCommand(token, gameID);
    getWebsocketCommunicator().send(leaveCommand);
  }

  public void makeMove(Integer gameID, ChessMove chessMove) throws ResponseException {
    MakeMoveCommand makeMoveCommand = new MakeMoveCommand(token, gameID, chessMove);
    getWebsocketCommunicator().send(makeMoveCommand);
  }

  public void resign(Integer gameID) throws ResponseException {
    ResignCommand resignCommand = new ResignCommand(token, gameID);
    getWebsocketCommunicator().send(resignCommand);
  }

  private WebsocketCommunicator getWebsocketCommunicator() throws ResponseException {
    try {
      if (websocketCommunicator == null) {
        websocketCommunicator = new WebsocketCommunicator("ws://" + domainName, serverMessageObserver);
      }
      return websocketCommunicator;
    } catch (Exception e) {
      throw new ResponseException(e.getMessage());
    }
  }
}
