package websocket.commands;

public class ResignCommand extends UserGameCommand {

  public Integer gameID;

  public ResignCommand(String authToken, Integer gameID) {
    super(authToken);
    this.gameID = gameID;
    this.commandType = CommandType.RESIGN;
  }

}
