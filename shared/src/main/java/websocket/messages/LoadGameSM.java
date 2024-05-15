package websocket.messages;

import chess.ChessGame;

public class LoadGameSM extends ServerMessage {

  private final ChessGame game;

  public LoadGameSM(ChessGame game) {
    super(ServerMessageType.LOAD_GAME);
    this.game = game;
  }

  public ChessGame getGame() {
    return game;
  }


}
