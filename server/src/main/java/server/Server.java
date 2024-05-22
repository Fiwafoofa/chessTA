package server;

import com.google.gson.Gson;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.mysql.SqlDAOFactory;
import model.AuthData;
import model.UserData;
import response.*;
import request.*;
import websocket.WebsocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

  private final DAOFactory daoFactory = new SqlDAOFactory();
  private final UserService userService = new UserService(daoFactory);
  private final GameService gameService = new GameService(daoFactory);
  private final ClearService clearService = new ClearService(daoFactory);
  private final Gson serializer = new Gson();

  public int run(int desiredPort) {
    Spark.port(desiredPort);

    Spark.staticFiles.location("web");
    Spark.notFound("<html><body style=\"color: blue\">My custom 404 page</body></html>");

    // Register your endpoints and handle exceptions here.
    WebsocketHandler websocketHandler = new WebsocketHandler();
    websocketHandler.setDaoFactory(daoFactory);
    Spark.webSocket("/ws", websocketHandler);

    Spark.delete("/db", (request, response) -> {
      clearService.clear();
      return "{}";
    });

    Spark.post("/user", (request, response) -> {
      UserData userData = serializer.fromJson(request.body(), UserData.class);
      AuthData authData = userService.register(userData);
      response.status(200);
      return serializer.toJson(authData);
    });

    Spark.post("/session", (request, response) -> {
      UserData userData = serializer.fromJson(request.body(), UserData.class);
      AuthData authData = userService.login(userData);
      return serializer.toJson(authData);
    });

    Spark.delete("/session", (request, response) -> {
      String token = request.headers("Authorization");
      userService.logout(token);
      return "{}";
    });

    Spark.get("/game", (request, response) -> {
      String token = request.headers("Authorization");
      ListGamesResponse listGamesResponse = gameService.listGames(token);
      return serializer.toJson(listGamesResponse);
    });

    Spark.post("/game", (request, response) -> {
      String token = request.headers("Authorization");
      CreateGameRequest createGameRequest = serializer.fromJson(request.body(), CreateGameRequest.class);
      CreateGameResponse createGameResponse = gameService.createGame(createGameRequest, token);
      return serializer.toJson(createGameResponse);
    });

    Spark.put("/game", (request, response) -> {
      String token = request.headers("authorization");
      JoinGameRequest joinGameRequest = serializer.fromJson(request.body(), JoinGameRequest.class);
      gameService.joinGame(joinGameRequest, token);
      return "{}";
    });

    Spark.exception(BadRequestException.class, this::exceptionHandler);
    Spark.exception(UnauthorizedException.class, this::exceptionHandler);
    Spark.exception(AlreadyTakenException.class, this::exceptionHandler);
    Spark.exception(DataAccessException.class, this::exceptionHandler);
    Spark.exception(InternalServerErrorException.class, this::exceptionHandler);

    Spark.awaitInitialization();
    return Spark.port();
  }

  private void exceptionHandler(Exception ex, Request request, Response response) {
    switch (ex) {
      case BadRequestException badRequestException -> response.status(400);
      case UnauthorizedException unauthorizedException -> response.status(401);
      case AlreadyTakenException alreadyTakenException -> response.status(403);
      default -> response.status(500);
    }
    response.body(serializer.toJson(new FailureResponse(ex.getMessage())));
  }

  public void stop() {
    Spark.stop();
    Spark.awaitStop();
  }
}