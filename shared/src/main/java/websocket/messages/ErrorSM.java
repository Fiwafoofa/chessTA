package websocket.messages;

public class ErrorSM extends ServerMessage {

  private final String errorMessage;

  public ErrorSM(String message) {
    super(ServerMessageType.ERROR);
    this.errorMessage = message;
  }

  public String getMessage() {
    return errorMessage;
  }
}
