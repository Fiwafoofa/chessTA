package ui;

import chess.*;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.HashSet;

public class BoardFormatter {
  
  private ChessBoard chessBoard;
  private TeamColor teamColor;
  private StringBuilder builder;
  private static final char[] LABELS = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', ' '};
  private static final String RESET = EscSeq.RESET_BG_COLOR + EscSeq.RESET_TEXT_COLOR;
  private static final String SPACING = " ";
  private BoardConfig config;

  private final Collection<ChessPosition> validPositions = new HashSet<>();
  private ChessPosition startPosition;

  public BoardFormatter() {
    config = new BoardConfig();
  }

  public void setBoardConfig(BoardConfig boardConfig) {
    config = boardConfig;
  }

  public String getFormattedBoard(ChessBoard board, TeamColor color, ChessPosition position) {
    if (position != null) {
      loadAllValidPositions(position);
    }
    startPosition = position;
    builder = new StringBuilder();
    teamColor = color;
    chessBoard = board;
    drawHeaderOrFooter();
    if (color == TeamColor.WHITE) {
      for (int i = 8; i >= 1; i--) {
        drawRow(i);
      }
    } else {
      for (int i = 1; i <= 8; i++) {
        drawRow(i);
      }
    }
    drawHeaderOrFooter();
    validPositions.clear();
    return builder.toString();
  }

  private void drawHeaderOrFooter() {
    builder.append(config.getEdgeBGColor()).append(config.getEdgeTextColor());
    if (teamColor == TeamColor.WHITE) {
      for (char label : LABELS) {
        builder.append(SPACING).append(label).append(SPACING);
      }
    } else {
      for (int i = LABELS.length-1; i >= 0; i--) {
        builder.append(SPACING).append(LABELS[i]).append(SPACING);
      }
    }
    builder.append(RESET).append('\n');
  }

  private void drawRow(Integer row) {
    drawSide(row);
    if (teamColor == TeamColor.WHITE) {
      for (int col = 1; col <= 8; col++) {
        drawSquare(row, col);
      }
    } else {
      for (int col = 8; col >= 1; col--) {
        drawSquare(row, col);
      }
    }
    drawSide(row);
    builder.append('\n');
  }

  private void drawSide(Integer row) {
    builder.append(config.getEdgeBGColor()).append(config.getEdgeTextColor());
    builder.append(SPACING).append(row).append(SPACING);
    builder.append(RESET);
  }

  private void drawSquare(Integer row, Integer col) {
    String backgroundColor = ((row % 2 == 1) != (col % 2 == 0))
      ? config.getBlackSquareBGColor()
      : config.getWhiteSquareBGColor();
    ChessPosition pos = new ChessPosition(row, col);
    if (!validPositions.isEmpty() && validPositions.contains(pos)) {
      backgroundColor = backgroundColor.equals(config.getBlackSquareBGColor())
          ? config.getHighlightBlackSquareBGColor()
          : config.getHighlightWhiteSquareBGColor();
    }
    if (pos.equals(startPosition)) {
      backgroundColor = config.getHighlightPositionBGColor();
    }
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
        builder.append(config.getWhiteTextColor());
      } else {
        builder.append(config.getBlackTextColor());
      }
    }
    builder.append(SPACING).append(pieceVal).append(SPACING);
  }

  private void loadAllValidPositions(ChessPosition position) {
    ChessGame game = new ChessGame();
    game.setBoard(chessBoard);
    Collection<ChessMove> validMoves = game.validMoves(position);
    for (ChessMove chessMove : validMoves) {
      validPositions.add(chessMove.getEndPosition());
    }
  }

  public static void main(String[] args) {
    ChessBoard chessBoard = new ChessBoard();
    chessBoard.resetBoard();
    String s = new BoardFormatter().getFormattedBoard(chessBoard, TeamColor.WHITE, null);
    System.out.println(s);

    s = new BoardFormatter().getFormattedBoard(chessBoard, TeamColor.BLACK, null);
    System.out.println(s);
  }

}
