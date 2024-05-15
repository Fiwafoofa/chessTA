package response;

public class CreateGameResponse {

  private Integer gameID;

  public CreateGameResponse(Integer gameID) {
    this.gameID = gameID;
  }

  public Integer getGameID() {
    return gameID;
  }
}
