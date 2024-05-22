package websocket;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConnectionManager {

  private final Gson serializer = new Gson();
  private final Map<Integer, List<Connection>> connections = new HashMap<>();

  public void broadcast(Integer gameID, String ignoredAuthToken, ServerMessage serverMessage) {
    List<Connection> gameConnections = connections.get(gameID);
    if (gameConnections == null) return;

    String serverMessageJson = serializer.toJson(serverMessage);
    for (Connection connection : gameConnections) {
      if (!connection.authToken().equals(ignoredAuthToken)) {
        try {
          connection.session().getRemote().sendString(serverMessageJson);
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }

  public void addConnection(Integer gameID, Connection connection) {
    List<Connection> gameConnections = connections.get(gameID);
    if (gameConnections == null) {
      gameConnections = new LinkedList<>();
      connections.put(gameID, gameConnections);
    }
    gameConnections.add(connection);
  }

  public void removeConnection(Integer gameID, String authToken) {
    List<Connection> gameConnections = connections.get(gameID);
    int index = -1;
    for (int i = 0; i < gameConnections.size(); i++) {
      if (gameConnections.get(i).authToken().equals(authToken)) {
        index = i;
        break;
      }
    }
    if (index != -1) {
      gameConnections.remove(index);
    }
  }


}
