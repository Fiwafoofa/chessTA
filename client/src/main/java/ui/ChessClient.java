package ui;

import chess.ChessPiece;
import model.GameData;
import net.ResponseException;
import net.ServerFacade;
import net.ServerMessageObserver;
import websocket.messages.ErrorSM;
import websocket.messages.LoadGameSM;
import websocket.messages.NotificationSM;

import java.util.*;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

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
      %s================%s WELCOME TO CHESS %s==================%s
      Help
      Move <a3> <a2> <optional promotion piece QUEEN>
      Highlight <a3>
      Redraw
      Leave
      Resign
      %s========================================================%s
      """, EscSeq.SET_TEXT_BOLD + EscSeq.SET_TEXT_COLOR_BLUE, 
      EscSeq.RESET_TEXT_COLOR + EscSeq.SET_TEXT_BLINKING,
      EscSeq.RESET_TEXT_BLINKING + EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.SET_TEXT_COLOR_WHITE,
      EscSeq.SET_TEXT_COLOR_BLUE,
      EscSeq.RESET_TEXT_COLOR);

  private enum State {
    PRE_LOGIN,
    POST_LOGIN,
    GAMEPLAY
  }

  private State uiState;
  private boolean stateChanged;
  private TeamColor teamColor;
  private ChessGame currChessGame;
  private Integer actualGameID;

  private final ServerFacade serverFacade;
  private final BoardFormatter boardFormatter;
  private final Map<Integer, Integer> displayGameIDsToRealGameIDs = new TreeMap<>();

  public ChessClient(String domainName) {
    serverFacade = new ServerFacade(domainName, this);
    uiState = State.PRE_LOGIN;
    stateChanged = true;
    boardFormatter = new BoardFormatter();
    teamColor = TeamColor.WHITE;
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

  @Override
  public void notify(NotificationSM notification) {
    System.out.println();
    System.out.println(notification.getMessage());
    System.out.print(">>> ");
  }

  @Override
  public void error(ErrorSM error) {
    printError(error.getMessage());
    System.out.print(">>> ");
  }

  @Override
  public void loadGame(LoadGameSM loadGame) {
    currChessGame = loadGame.getGame();
    System.out.println();
    System.out.println(boardFormatter.getFormattedBoard(currChessGame.getBoard(), teamColor, null));
    System.out.print(">>>");
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

  // PRE LOGIN
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
    changeUIState(State.POST_LOGIN);
  }

  private void login(String username, String password) throws ResponseException {
    serverFacade.login(username, password);
    changeUIState(State.POST_LOGIN);
  }

  // POST LOGIN
  private void evalPostLogin(String... args) throws UIException, ResponseException {
    String cmd = args[0];
    switch (cmd) {
      case "create" -> {
        verifyArgumentCounts(2, "<game name>", args);
        createGame(args[1]);
      }
      case "list" -> listGames();
      case "join" -> {
        verifyArgumentCounts(3, "<game id> <team color>", args);
        joinGame(
            Integer.parseInt(args[1]),
            args[2]
        );
      }
      case "logout" -> logout();
      case "observe" -> {
        verifyArgumentCounts(2, "<game id>");
        joinObserver(Integer.parseInt(args[1]));
      }
      default -> System.out.print(POST_LOGIN_MENU);
    }
  }

  private void logout() throws ResponseException {
    serverFacade.logout();
    changeUIState(State.PRE_LOGIN);
  }

  private void createGame(String gameName) throws ResponseException {
    serverFacade.createGame(gameName);
    printHelp();
    System.out.println("Created " + gameName);
  }

  private void listGames() throws ResponseException {
    Collection<GameData> games = serverFacade.listGames();
    Integer gameIdCounter = 1;
    printHelp();
    System.out.println("Games:\n-------------");
    for (GameData game : games) {
      displayGameIDsToRealGameIDs.put(gameIdCounter, game.gameID());
      System.out.printf(
          "%d: %s, White: %s - Black: %s%n",
        gameIdCounter,
        game.gameName(),
        game.whiteUsername(),
        game.blackUsername()
      );
      gameIdCounter++;
    }
    System.out.println("---------------");
  }

  private void joinGame(Integer gameID, String teamColor) throws ResponseException {
    this.teamColor = teamColor.equals("white") ? TeamColor.WHITE : TeamColor.BLACK;
    this.actualGameID = displayGameIDsToRealGameIDs.get(gameID);
    serverFacade.joinGame(actualGameID, teamColor);
    changeUIState(State.GAMEPLAY);
  }

  private void joinObserver(Integer gameID) throws ResponseException {
    this.actualGameID = displayGameIDsToRealGameIDs.get(gameID);
    serverFacade.joinObserver(actualGameID);
    changeUIState(State.GAMEPLAY);
  }

  // GAMEPLAY
  private void evalGameplay(String... args) throws ResponseException, UIException {
    String cmd = args[0];
    switch (cmd) {
      case "help" -> printHelp();
      case "redraw" -> System.out.println(boardFormatter.getFormattedBoard(currChessGame.getBoard(), teamColor, null));
      case "leave" -> {
        serverFacade.leaveGame(actualGameID);
        changeUIState(State.POST_LOGIN);
      }
      case "resign" -> serverFacade.resign(actualGameID);
      case "move" -> {
        verifyArgumentCounts(3, "<start pos> <end pos> <optional promotion>", args);
        String promotionPieceStr = args.length == 4 ? args[3] : null;
        makeMove(args[1], args[2], promotionPieceStr);
      }
      case "highlight" -> {
        verifyArgumentCounts(2, "<position>", args);
        highlight(args[1]);
      }
      default -> {
        System.out.println("Invalid command: " + Arrays.toString(args));
        printHelp();
      }
    }
  }

  private void makeMove(
      String startPosString,
      String endPosString,
      String promotionString
  ) throws UIException, ResponseException {
    ChessPosition startPos = parseChessPosition(startPosString);
    ChessPosition endPos = parseChessPosition(endPosString);
    ChessPiece.PieceType promotionPiece = promotionString == null ? null : parsePromotionPiece(promotionString);
    ChessMove chessMove = new ChessMove(startPos, endPos, promotionPiece);
    serverFacade.makeMove(actualGameID, chessMove);
  }


  private void highlight(String positionString) throws UIException {
    ChessPosition position = parseChessPosition(positionString);
    System.out.println(boardFormatter.getFormattedBoard(currChessGame.getBoard(), teamColor, position));
  }

  private ChessPosition parseChessPosition(String chessPositionString) throws UIException {
    if (chessPositionString.length() != 2) throw new UIException("Invalid position format: " + chessPositionString);
    return new ChessPosition(
        Integer.parseInt(String.valueOf(chessPositionString.charAt(1))),
        chessPositionString.charAt(0)
    );
  }

  private ChessPiece.PieceType parsePromotionPiece(String promotionPieceString) {
    return switch (promotionPieceString) {
      case "PAWN" -> ChessPiece.PieceType.PAWN;
      case "BISHOP" -> ChessPiece.PieceType.BISHOP;
      case "ROOK" -> ChessPiece.PieceType.ROOK;
      case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
      case "QUEEN" -> ChessPiece.PieceType.QUEEN;
      default -> null;
    };
  }

  private void printHelp() {
    switch (uiState) {
      case PRE_LOGIN -> System.out.print(PRE_LOGIN_MENU);
      case POST_LOGIN -> System.out.print(POST_LOGIN_MENU);
      case GAMEPLAY -> {
        System.out.print(GAMEPLAY_MENU);
        if (currChessGame != null) {
          System.out.println(boardFormatter.getFormattedBoard(currChessGame.getBoard(), teamColor, null));
        }
      }
    }
  }

  private void printError(String message) {
    System.out.printf(
        """
            %sERROR: %s%s
            %n""",
      EscSeq.SET_TEXT_COLOR_RED, message, EscSeq.RESET_TEXT_COLOR
    );
  }

  private void verifyArgumentCounts(
    Integer numArgs, 
    String parameterMessage, 
    String... args
  ) throws UIException {
    if (args.length < numArgs) {
      numArgs--;
      throw new UIException(String.format(
          "Invalid num of arguments. Expected %d parameters: %s",
          numArgs,
          parameterMessage)
      );
    }
  }

  private void changeUIState(State uiState) {
    this.uiState = uiState;
    this.stateChanged = true;
  }

}
