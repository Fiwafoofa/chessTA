package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

public class BoardFormatter {
  
  private ChessBoard chessBoard;
  private TeamColor teamColor;
  private StringBuilder builder;

  private char[] labels = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', ' '};
  private String edgeBGColor = EscSeq.SET_BG_COLOR_DARK_GREY;
  private String edgeTextColor = EscSeq.SET_TEXT_COLOR_WHITE;
  private String reset = EscSeq.RESET_BG_COLOR + EscSeq.RESET_TEXT_COLOR;
  private String whiteSquareBGColor = EscSeq.SET_BG_COLOR_WHITE;
  private String whiteTextColor = EscSeq.SET_TEXT_COLOR_BLUE;
  private String blackSquareBGColor = EscSeq.SET_BG_COLOR_BLACK;
  private String blackTextColor = EscSeq.SET_TEXT_COLOR_RED;
  
  // orientation things
  private Integer start, end, valueUpdater, labelStart, labelEnd;
  private ValueComparer comparer, labelComparer;

  private static final String SPACING = " ";


  public String getFormattedBoard(ChessBoard board, TeamColor color) {
    builder = new StringBuilder();
    teamColor = color;
    chessBoard = board;

    setupOrientation();
    drawHeaderOrFooter();
    for (int i = start; comparer.compareValues(i, end); i += valueUpdater) {
      drawRow(i);
    }
    drawHeaderOrFooter();

    return builder.toString();
  }

  private void setupOrientation() {
    if (teamColor == TeamColor.WHITE) {
      start = 8;
      end = 1;
      valueUpdater = -1;
      comparer = (int x, int y) -> x >= y;
      labelStart = 0;
      labelEnd = labels.length - 1;
      labelComparer = (int x, int y) -> x <= y;
    } else {
      start = 1;
      end = 8;
      valueUpdater = 1;
      comparer = (int x, int y) -> x <= y;
      labelStart = labels.length - 1;
      labelEnd = 0;
      labelComparer = (int x, int y) -> x >= y;
    }
  }

  private void drawHeaderOrFooter() {
    builder.append(edgeBGColor + edgeTextColor);
    for (int row = labelStart; labelComparer.compareValues(row, labelEnd); row += (valueUpdater * -1)) {
      builder.append(SPACING + labels[row] + SPACING);
    }
    builder.append(reset).append('\n');
  }

  private void drawRow(Integer row) {
    drawSide(row);
    for (int col = start; comparer.compareValues(col, end); col += valueUpdater) {
      drawSquare(row, col);
    }
    drawSide(row);
    builder.append('\n');
  }

  private void drawSide(Integer row) {
    builder.append(edgeBGColor + edgeTextColor);
    builder.append(SPACING + row + SPACING);
    builder.append(reset);
  }

  private void drawSquare(Integer row, Integer col) {
    String backgroundColor = ((row % 2 == 1) == (col % 2 == 0)) 
      ? blackSquareBGColor 
      : whiteSquareBGColor;
    builder.append(backgroundColor);
    drawPieceChar(row, col);
  }

  private void drawPieceChar(Integer row, Integer col) {
    ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
    String pieceVal;
    if (piece == null) {
      pieceVal = SPACING;
    } else {
      pieceVal = switch (piece.getPieceType()) {
        case ChessPiece.PieceType.PAWN -> "P";
        case ChessPiece.PieceType.ROOK -> "R";
        case ChessPiece.PieceType.KNIGHT -> "N";
        case ChessPiece.PieceType.BISHOP -> "B";
        case ChessPiece.PieceType.QUEEN -> "Q";
        case ChessPiece.PieceType.KING -> "K";
      };

      if (piece.getTeamColor() == TeamColor.WHITE) {
        builder.append(whiteTextColor);
      } else {
        builder.append(blackTextColor);
      }
    }
    builder.append(SPACING + pieceVal + SPACING);
  }

  @FunctionalInterface
  public static interface ValueComparer {
    boolean compareValues(int x, int y);
  }

  public static void main(String[] args) {
    ChessBoard chessBoard = new ChessBoard();
    chessBoard.resetBoard();
    String s = new BoardFormatter().getFormattedBoard(chessBoard, TeamColor.WHITE);
    System.out.println(s);

    s = new BoardFormatter().getFormattedBoard(chessBoard, TeamColor.BLACK);
    System.out.println(s);
  }

}
