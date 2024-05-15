package websocket.messages;

public class NotificationSM extends ServerMessage {

  private final String message;

  public NotificationSM(String message) {
    super(ServerMessageType.LOAD_GAME);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
