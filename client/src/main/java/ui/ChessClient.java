package ui;

import model.GameData;
import net.ResponseException;
import net.ServerFacade;
import net.ServerMessageObserver;
import websocket.messages.ErrorSM;
import websocket.messages.LoadGameSM;
import websocket.messages.NotificationSM;

import java.util.Collection;
import java.util.Scanner;

public class ChessClient implements ServerMessageObserver {

  private static final String PRE_LOGIN_MENU = String.format("""
      %s================%s WELCOME TO CHESS %s==================%s
      Type one of the commands below to get started.
      Note that the angled brackets are not needed
        Help
        Login <username> <password>
        Register <username> <password> <email>
        Quit 
      %s========================================================%s
      """, EscSeq.SET_TEXT_BOLD + EscSeq.SET_TEXT_COLOR_BLUE, 
      EscSeq.RESET_TEXT_COLOR + EscSeq.SET_TEXT_BLINKING,
      EscSeq.RESET_TEXT_BLINKING + EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.SET_TEXT_COLOR_WHITE,
      EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.RESET_TEXT_COLOR
      );

  private static final String POST_LOGIN_MENU = String.format("""
      %s================%s WELCOME TO CHESS %s==================%s
        Help
        Create <game name>
        List
        Join <game id> <team color (white/black)>
        Observe <game id>
        Logout
      %s========================================================%s
      """, EscSeq.SET_TEXT_BOLD + EscSeq.SET_TEXT_COLOR_BLUE, 
      EscSeq.RESET_TEXT_COLOR + EscSeq.SET_TEXT_BLINKING,
      EscSeq.RESET_TEXT_BLINKING + EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.SET_TEXT_COLOR_WHITE,
      EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.RESET_TEXT_COLOR
      );

  private static final String GAMEPLAY_MENU = String.format("""
      hi there :)
      """);

  private enum State {
    PRE_LOGIN,
    POST_LOGIN,
    GAMEPLAY
  }

  private State uiState;
  private boolean stateChanged;

  private final ServerFacade serverFacade;

  public ChessClient(String domainName) {
    serverFacade = new ServerFacade(domainName, this);
    uiState = State.PRE_LOGIN;
    stateChanged = true;
  }

  public void run() {
    Scanner scanner = new Scanner(System.in);
    String userInput;
    String[] arguments;

    while (true) {
      if (stateChanged) {
        printHelp();
        stateChanged = false;
      }
      System.out.print(">>> ");
      userInput = scanner.nextLine();
      arguments = userInput.toLowerCase().split(" ");
      try {
        System.out.println(EscSeq.ERASE_SCREEN);
        eval(arguments);
      } catch (Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("quit")) {
          System.out.println("Goodbye!");
          break;
        }
        printHelp();
        printError(e.getMessage());
      }
    }
    scanner.close();
  }

  private void eval(String... args) throws UIException, ResponseException {
    if (args.length < 1) {
      throw new UIException("Command not found");
    }
    switch (uiState) {
      case PRE_LOGIN -> evalPreLogin(args);
      case POST_LOGIN -> evalPostLogin(args);
      case GAMEPLAY -> evalGameplay(args);
    }
  }

  private void evalPreLogin(String... args) throws UIException, ResponseException {
    String cmd = args[0];
    switch (cmd) {
      case "register" -> {
        verifyArgumentCounts(4, "<username> <password> <email>", args);
        register(args[1], args[2], args[3]);
      }
      case "login" -> {
        verifyArgumentCounts(3, "<username> <password>", args);
        login(args[1], args[2]);
      }
      case "quit" -> throw new UIException("quit");
      default -> System.out.println(PRE_LOGIN_MENU);
    }
  }

  private void register(String username, String password, String email) throws ResponseException {
    serverFacade.register(username, password, email);
    uiState = State.POST_LOGIN;
    stateChanged = true;
  }

  private void login(String username, String password) throws ResponseException {
    serverFacade.login(username, password);
    uiState = State.POST_LOGIN;
    stateChanged = true;
  }

  private void evalPostLogin(String... args) throws UIException, ResponseException {
    String cmd = args[0];
    switch (cmd) {
      case "create" -> {
        verifyArgumentCounts(2, "<game name>", args);
        createGame(args[1]);
      }
      case "list" -> {
        listGames();
      }
      case "join" -> {
        verifyArgumentCounts(3, "<game id> <team color>", args);
        joinGame(
            Integer.parseInt(args[1]),
            args[2]
        );
      }
      case "logout" -> {
        logout();
      }
      case "observe" -> {
        verifyArgumentCounts(2, "<game id>");
        joinObserver(Integer.parseInt(args[1]));
      }
      default -> System.out.print(POST_LOGIN_MENU);
    }
  }

  private void logout() throws ResponseException {
    serverFacade.logout();
    uiState = State.PRE_LOGIN;
    stateChanged = true;
  }

  private void createGame(String gameName) throws ResponseException {
    Integer gameID = serverFacade.createGame(gameName);
    printHelp();
    System.out.println("Game created with game id `" + gameID + '`');
  }

  private void listGames() throws ResponseException {
    Collection<GameData> games = serverFacade.listGames();
    System.out.println("Games: -------------");
    for (GameData game : games) {
      System.out.println(
        String.format(
          "%d: %s, W: %s B: %s", 
          game.gameID(), 
          game.gameName(), 
          game.whiteUsername(), 
          game.blackUsername()
        )
      );
    }
    System.out.println("---------------");
  }

  private void joinGame(Integer gameID, String teamColor) throws ResponseException {
    serverFacade.joinGame(gameID, teamColor);
  }

  private void joinObserver(Integer gameID) {
    return;
  }

  private void evalGameplay(String... args) {
    return;
  }

  private void printHelp() {
    switch (uiState) {
      case PRE_LOGIN -> System.out.print(PRE_LOGIN_MENU);
      case POST_LOGIN -> System.out.print(POST_LOGIN_MENU);
      case GAMEPLAY -> System.out.print(GAMEPLAY_MENU);
    }
  }

  private void printError(String message) {
    System.out.println(
      String.format(
        """
        ERROR: %s
        """, 
        message
      )
    );
  }

  private void verifyArgumentCounts(
    Integer numArgs, 
    String parameterMessage, 
    String... args
  ) throws UIException {
    if (args.length != numArgs) {
      numArgs--;
      throw new UIException(String.format(
          "Invalid num of arguments. Expected %d parameters: %s",
          numArgs,
          parameterMessage)
      );
    }
  }

  @Override
  public void notify(NotificationSM notification) {

  }

  @Override
  public void error(ErrorSM error) {

  }

  @Override
  public void loadGame(LoadGameSM loadGame) {

  }
}
