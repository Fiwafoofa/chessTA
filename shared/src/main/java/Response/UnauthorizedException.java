package Response;

public class UnauthorizedException extends Exception {

  public UnauthorizedException() {
    super("Error: unauthorized");
  }
  public UnauthorizedException(String message) {
    super(message);
  }
}
