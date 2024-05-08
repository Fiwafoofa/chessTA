package ui;

import chess.ChessGame;
import net.ServerFacade;
import net.ServerMessageObserver;
import websocket.messages.ErrorSM;
import websocket.messages.LoadGameSM;
import websocket.messages.NotificationSM;

import java.util.Scanner;

public class ChessClient implements ServerMessageObserver {

  private static final String PRE_LOGIN_MENU = String.format("""
      %s================%s WELCOME TO CHESS %s==================%s
      Type one of the commands below to get started.
      Note that the angled brackets are not needed
      %s Help %s
      %s Login %s <username> <password> <email>
      %s Register %s <username> <password>
      %s Quit %s
      %s========================================================%s
      """, EscSeq.SET_TEXT_BOLD + EscSeq.SET_TEXT_COLOR_BLUE, EscSeq.RESET_TEXT_COLOR + EscSeq.SET_TEXT_BLINKING);

  private static final String POST_LOGIN_MENU = String.format("""
      %s================%s WELCOME TO CHESS %s==================%s
      %s Help %s
      %s Create %s
      %s List %s
      %s Join %s
      %s Observe %s
      %s Logout %s
      %s========================================================%s
      """);

  private static final String GAMEPLAY_MENU = String.format("""
      
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

    do {
      if (stateChanged) {
        printHelp();
        stateChanged = false;
      }
      System.out.print(">>> ");
      userInput = scanner.nextLine();
      arguments = userInput.toLowerCase().split(" ");
      try {
        eval(arguments);
      } catch (UIException e) {
        if (e.getMessage().contains("quit")) {
          System.out.println("Goodbye!");
          return;
        }
        System.out.println(e.getMessage());
      }
    } while (!userInput.equals("quit"));
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

  private void eval(String... args) throws UIException {
    if (args.length < 1) {
      throw new UIException("Command not found");
    }
    switch (uiState) {
      case PRE_LOGIN -> evalPreLogin(args);
      case POST_LOGIN -> evalPostLogin(args);
      case GAMEPLAY -> evalGameplay(args);
    }
  }

  private void evalPreLogin(String... args) throws UIException {
    String cmd = args[0];
    switch (cmd) {
      case "register" -> {
        verifyArguments(4, "<username> <password> <email>", args);
        register(args[1], args[2], args[3]);
      }
      case "login" -> {
        verifyArguments(3, "<username> <password>", args);
        login(args[1], args[2]);
      }
      case "quit" -> throw new UIException("quit");
      default -> System.out.println(PRE_LOGIN_MENU);
    }
  }

  private void register(String username, String password, String email) {
    System.out.println("You registered!");
    uiState = State.POST_LOGIN;
    stateChanged = true;
  }

  private void login(String username, String password) {
    System.out.println("You logged in!");
    uiState = State.POST_LOGIN;
    stateChanged = true;
  }

  private void evalPostLogin(String... args) throws UIException {
    String cmd = args[0];
    switch (cmd) {
      case "create" -> {
        verifyArguments(2, "<game name>", args);
        createGame(args[1]);
      }
      case "list" -> {
        listGames();
      }
      case "join" -> {
        verifyArguments(3, "<game id> <team color>", args);
        joinGame(
            Integer.parseInt(args[1]),
            args[2].equals("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK
        );
      }
      case "logout" -> {
        logout();
      }
      case "observe" -> {
        verifyArguments(2, "<game id>");
        joinObserver(Integer.parseInt(args[1]));
      }
    }
  }

  private void logout() {
    System.out.println("YOU LOGGED OUT!");
    uiState = State.PRE_LOGIN;
    stateChanged = true;
  }

  private void createGame(String gameName) {

  }

  private void listGames() {

  }

  private void joinGame(Integer gameID, ChessGame.TeamColor teamColor) {

  }

  private void joinObserver(Integer gameID) {

  }

  private void evalGameplay(String... args) {
    // TODO: in phase 6
  }

  private void printHelp() {
    switch (uiState) {
      case PRE_LOGIN -> System.out.print(PRE_LOGIN_MENU);
      case POST_LOGIN -> System.out.print(POST_LOGIN_MENU);
      case GAMEPLAY -> System.out.print(GAMEPLAY_MENU);
    }
  }

  private void verifyArguments(Integer numArgs, String parameterMessage, String... args) throws UIException {
    if (args.length != numArgs) {
      numArgs--;
      throw new UIException(String.format(
          "Invalid num of arguments. Expected %d parameters: %s",
          numArgs,
          parameterMessage)
      );
    }
  }
}
