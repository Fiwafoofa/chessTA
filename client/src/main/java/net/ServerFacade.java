package net;

public class ServerFacade {

  private final HttpCommunicator httpCommunicator;
  private final WebsocketCommunicator websocketCommunicator;

  public ServerFacade(String domainName, ServerMessageObserver serverMessageObserver) {
    httpCommunicator = new HttpCommunicator("http://" + domainName);
    websocketCommunicator = new WebsocketCommunicator("ws://" + domainName, serverMessageObserver);
  }
}
