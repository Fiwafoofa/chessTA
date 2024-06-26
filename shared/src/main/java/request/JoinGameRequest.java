package request;

public record JoinGameRequest(
    String playerColor,
    Integer gameID
) {
}
