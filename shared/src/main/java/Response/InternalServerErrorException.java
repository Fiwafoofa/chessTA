package Response;

public class InternalServerErrorException extends Exception {

  public InternalServerErrorException() {
    super("Error: description message");
  }
  public InternalServerErrorException(String message) {
    super(message);
  }
}
