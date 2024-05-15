package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

public class ServerFacadeTests {

  private static Server server;

  @BeforeAll
  public static void init() {
    server = new Server();
    var port = server.run(0);
    System.out.println("Started test HTTP server on " + port);
  }

  @AfterAll
  static void stopServer() {
    server.stop();
  }


  @Test
  public void sampleTest() {
    Assertions.assertTrue(true);
  }
}