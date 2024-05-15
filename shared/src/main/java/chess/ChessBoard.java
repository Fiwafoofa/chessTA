package chess;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public static final Integer BOARD_SIZE = 8;

    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
//        putSpecialPieces(ChessGame.TeamColor.BLACK, BOARD_SIZE);
        fillPieces(ChessGame.TeamColor.BLACK, 8, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK);
        putPawns(ChessGame.TeamColor.BLACK, 7);
        putPawns(ChessGame.TeamColor.WHITE, 2);
        fillPieces(ChessGame.TeamColor.WHITE, 1, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK);
//        putSpecialPieces(ChessGame.TeamColor.WHITE, 1);

    }

    private void fillPieces(ChessGame.TeamColor teamColor, Integer row, ChessPiece.PieceType... pieces) {
        for (int i = 1; i <= pieces.length; i++) {
            addPiece(new ChessPosition(row, i), new ChessPiece(teamColor, pieces[i-1]));
        }
    }

    private void putSpecialPieces(ChessGame.TeamColor teamColor, Integer row) {
        addPiece(new ChessPosition(row, 1), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
    }

    private void putPawns(ChessGame.TeamColor teamColor, Integer row) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }

    public boolean isOutOfBounds(ChessPosition position) {
        return position.getRow() < 1
            || position.getRow() > BOARD_SIZE
            || position.getColumn() < 1
            || position.getColumn() > BOARD_SIZE;
    }

    private class ChessBoardIterator implements Iterator<ChessPiece> {

        private Integer colIndex = 0;
        private Integer rowIndex = 0;

        @Override
        public boolean hasNext() {
            return rowIndex < BOARD_SIZE;
        }

        @Override
        public ChessPiece next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            ChessPiece chessPiece = board[rowIndex][colIndex];
            colIndex++;
            if (colIndex == 8) {
                colIndex = 0;
                rowIndex++;
            }
            return chessPiece;
        }
    }

    public Iterator<ChessPiece> getIterator() {
        return new ChessBoardIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        ChessPiece chessPiece;
        String pieceStr;
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col ++) {
                chessPiece = getPiece(new ChessPosition(row, col));
                stringBuilder.append(' ');
                if (chessPiece == null) {
                    stringBuilder.append("  ");
                    continue;
                }
                pieceStr = switch (chessPiece.getPieceType()) {
                    case KING -> "K";
                    case QUEEN -> "Q";
                    case ROOK -> "R";
                    case KNIGHT -> "N";
                    case PAWN -> "P";
                    case BISHOP -> "B";
                };
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK) pieceStr = pieceStr.toLowerCase();
                stringBuilder.append(pieceStr);
                stringBuilder.append(' ');
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        for (Iterator<ChessPiece> it = chessBoard.getIterator(); it.hasNext(); ) {
            ChessPiece piece = it.next();
            System.out.println(piece);
        }
    }
}
