package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DAOFactory;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import response.BadRequestException;
import response.UnauthorizedException;
import websocket.commands.*;
import websocket.messages.*;

@WebSocket
public class WebsocketHandler {

  private final Gson serializer = new Gson();
  private final ConnectionManager connectionManager = new ConnectionManager();

  private DAOFactory daoFactory;

  public void setDaoFactory(DAOFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    try {
      UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);
      if (daoFactory.getAuthDAO().getAuthData(command.getAuthString()) == null) {
        throw new UnauthorizedException();
      }

      switch (command.getCommandType()) {
        case CONNECT -> {
          ConnectCommand connectCommand = serializer.fromJson(message, ConnectCommand.class);
          handleConnect(session, connectCommand);
        }
        case LEAVE -> {
          LeaveCommand leaveCommand = serializer.fromJson(message, LeaveCommand.class);
          handleLeave(leaveCommand);
        }
        case RESIGN -> {
          ResignCommand resignCommand = serializer.fromJson(message, ResignCommand.class);
          handleResign(resignCommand);
        }
        case MAKE_MOVE -> {
          MakeMoveCommand makeMoveCommand = serializer.fromJson(message, MakeMoveCommand.class);
          handleMakeMove(makeMoveCommand);
        }
      }
    } catch (Exception e) {
      ErrorSM errorSM = new ErrorSM(e.getMessage());
      session.getRemote().sendString(serializer.toJson(errorSM));
    }

  }

  @OnWebSocketError
  public void onError(Throwable throwable) {
    System.out.println(throwable.getMessage());
  }

  private void handleConnect(Session session, ConnectCommand connectCommand) throws Exception {
    // 1
    GameData gameData = daoFactory.getGameDAO().getGameData(connectCommand.gameID);
    if (gameData == null) throw new BadRequestException("Game does not exist");

    LoadGameSM loadGameSM = new LoadGameSM(gameData.game());
    session.getRemote().sendString(serializer.toJson(loadGameSM));

    // 2
    AuthData authData = daoFactory.getAuthDAO().getAuthData(connectCommand.getAuthString());
    NotificationSM notification = getNotification(authData, gameData);
    connectionManager.broadcast(connectCommand.gameID, connectCommand.getAuthString(), notification);
    connectionManager.addConnection(connectCommand.gameID, new Connection(connectCommand.getAuthString(), session));

  }

  private static NotificationSM getNotification(AuthData authData, GameData gameData) {
    String connectionMessage = authData.username();
    if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(authData.username())) {
      connectionMessage += " joined as the white player";
    } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(authData.username())) {
      connectionMessage += " joined as the black player";
    } else {
      connectionMessage += " joined as an observer";
    }

    return new NotificationSM(connectionMessage);
  }

  private void handleLeave(LeaveCommand leaveCommand) throws Exception {
    // 1
    AuthData authData = daoFactory.getAuthDAO().getAuthData(leaveCommand.getAuthString());
    GameData gameData = daoFactory.getGameDAO().getGameData(leaveCommand.gameID);
    if (authData.username().equals(gameData.whiteUsername())) {
      GameData newGameData = new GameData(
          gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()
      );
      daoFactory.getGameDAO().updateGameData(newGameData);
    } else if (authData.username().equals(gameData.blackUsername())) {
      GameData newGameData = new GameData(
          gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()
      );
      daoFactory.getGameDAO().updateGameData(newGameData);
    }

    // 2
    connectionManager.removeConnection(leaveCommand.gameID, leaveCommand.getAuthString());
    NotificationSM notification = new NotificationSM(authData.username() + " has left the game");
    connectionManager.broadcast(leaveCommand.gameID, leaveCommand.getAuthString(), notification);


  }

  private void handleResign(ResignCommand resignCommand) throws Exception {
    // verify person can resign
    AuthData authData = daoFactory.getAuthDAO().getAuthData(resignCommand.getAuthString());
    GameData gameData = daoFactory.getGameDAO().getGameData(resignCommand.gameID);

    if (gameData.game().isGameOver()) {
      throw new Exception("Game has already ended");
    }

    if (!authData.username().equals(gameData.whiteUsername()) &&
        !authData.username().equals(gameData.blackUsername())) {
      throw new Exception("Observer cannot resign");
    }


    // 1
    gameData.game().setGameOver(true);
    daoFactory.getGameDAO().updateGameData(gameData);

    // 2
    NotificationSM notificationSM = new NotificationSM(authData.username() + " has resigned the game");
    connectionManager.broadcast(resignCommand.gameID, null, notificationSM);

  }

  private void handleMakeMove(MakeMoveCommand makeMoveCommand) throws Exception {
    GameData gameData = daoFactory.getGameDAO().getGameData(makeMoveCommand.gameID);
    AuthData authData = daoFactory.getAuthDAO().getAuthData(makeMoveCommand.getAuthString());

    if (gameData.game().isGameOver()) {
      throw new Exception("Game is over. Go home newbie");
    }

    if (!authData.username().equals(gameData.whiteUsername()) &&
        !authData.username().equals(gameData.blackUsername())) {
      throw new Exception("Observer cannot make a move");
    }

    // 1
    ChessGame chessGame = gameData.game();
    if (authData.username().equals(gameData.whiteUsername()) && chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK) {
      throw new Exception("Can't make a move for the black team");
    } else if (authData.username().equals(gameData.blackUsername()) && chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) {
      throw new Exception("Can't make a move for the white team");
    } // could use an or I don't care
    try {
      chessGame.makeMove(makeMoveCommand.move);
      if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) || chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
        // should do stalemate check but I don't care
        chessGame.setGameOver(true);
      }

    } catch (InvalidMoveException e) {
      throw new Exception("Invalid move");
    }

    // 2
    daoFactory.getGameDAO().updateGameData(gameData);

    // 3
    LoadGameSM loadGameSM = new LoadGameSM(chessGame);
    connectionManager.broadcast(makeMoveCommand.gameID, null, loadGameSM);

    // 4
    NotificationSM notificationSM = new NotificationSM(authData.username() + " made a move");
    connectionManager.broadcast(makeMoveCommand.gameID, makeMoveCommand.getAuthString(), notificationSM);

    // 5
    String team;
    if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
      team = chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ? "White" : "Black";
      notificationSM = new NotificationSM(team + " is in checkmate");
      connectionManager.broadcast(makeMoveCommand.gameID, null, notificationSM);
    }  else if (chessGame.isInCheck(ChessGame.TeamColor.WHITE) || chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
      team = chessGame.isInCheck(ChessGame.TeamColor.WHITE) ? "White" : "Black";
      notificationSM = new NotificationSM(team + " is in check");
      connectionManager.broadcast(makeMoveCommand.gameID, null, notificationSM);
    }

  }

}
