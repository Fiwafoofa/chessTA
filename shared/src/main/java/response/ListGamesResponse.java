package response;

import model.GameData;

import java.util.Collection;

public class ListGamesResponse {

  public Collection<GameData> games;

  public ListGamesResponse(Collection<GameData> games) {
    this.games = games;
  }
}
