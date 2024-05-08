package response;

public class BadRequestException extends Exception {

  public BadRequestException() {
    super("Error: bad request");
  }
  public BadRequestException(String message) {
    super(message);
  }
}
