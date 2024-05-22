package websocket.commands;

import chess.ChessMove;

public class LeaveCommand extends UserGameCommand {

  public Integer gameID;

  public LeaveCommand(String authToken, Integer gameID) {
    super(authToken);
    this.gameID = gameID;
    this.commandType = CommandType.LEAVE;
  }

}
