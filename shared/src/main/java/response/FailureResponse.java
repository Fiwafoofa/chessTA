package response;

public class FailureResponse {

  private String message;

  public FailureResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
