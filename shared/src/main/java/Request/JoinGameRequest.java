package Request;

public record JoinGameRequest(
    String playerColor,
    Integer gameID
) {
}
