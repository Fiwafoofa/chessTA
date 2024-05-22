package websocket.commands;

public class ConnectCommand extends UserGameCommand {

  public Integer gameID;
  public ConnectCommand(String authToken, Integer gameID) {
    super(authToken);
    this.gameID = gameID;
    this.commandType = CommandType.CONNECT;
  }
}
