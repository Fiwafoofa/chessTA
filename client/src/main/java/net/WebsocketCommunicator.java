package net;

import java.net.URI;
import javax.websocket.*;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class WebsocketCommunicator extends Endpoint {

  private final Gson serializer = new Gson();
  private final ServerMessageObserver observer;
  private Session session;

  public WebsocketCommunicator(String serverUrl, ServerMessageObserver serverMessageObserver) throws Exception {
    observer = serverMessageObserver;
    URI uri = new URI(serverUrl + "/ws");
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    this.session = container.connectToServer(this, uri);

    this.session.addMessageHandler(new MessageHandler.Whole<String>() {

      @Override
      public void onMessage(String serverMessageJson) {
        ServerMessage serverMessage = serializer.fromJson(serverMessageJson, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
          case ServerMessage.ServerMessageType.LOAD_GAME -> {
            LoadGameSM loadGameSM = serializer.fromJson(serverMessageJson, LoadGameSM.class);
            observer.loadGame(loadGameSM);
          }
          case ServerMessage.ServerMessageType.ERROR -> {
            ErrorSM errorSM = serializer.fromJson(serverMessageJson, ErrorSM.class);
            observer.error(errorSM);
          }
          case ServerMessage.ServerMessageType.NOTIFICATION -> {
            NotificationSM notificationSM = serializer.fromJson(
              serverMessageJson, 
              NotificationSM.class
            );
            observer.notify(notificationSM);
          }
        }
      }
    });
  }

  public void send(UserGameCommand userGameCommand) throws ResponseException {
    try {
      this.session.getBasicRemote().sendText(serializer.toJson(userGameCommand));
    } catch (Exception e) {
      throw new ResponseException(e.getMessage());
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig config) {
    System.out.println("Opening connection");
  }
}
