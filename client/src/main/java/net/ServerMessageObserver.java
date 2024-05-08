package net;

import websocket.messages.ErrorSM;
import websocket.messages.LoadGameSM;
import websocket.messages.NotificationSM;

public interface ServerMessageObserver {
  void notify(NotificationSM notification);
  void error(ErrorSM error);
  void loadGame(LoadGameSM loadGame);
}
