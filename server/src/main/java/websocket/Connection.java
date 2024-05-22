package websocket;

import org.eclipse.jetty.websocket.api.Session;

public record Connection(
    String authToken,
    Session session
) {}
