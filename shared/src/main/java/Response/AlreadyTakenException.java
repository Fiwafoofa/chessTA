package Response;

public class AlreadyTakenException extends Exception {

  public AlreadyTakenException() {
    super("Error: already taken");
  }
  public AlreadyTakenException(String message) {
    super(message);
  }
}
