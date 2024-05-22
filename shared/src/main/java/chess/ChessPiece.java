package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType type;
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        this.type = type;
        hasMoved = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece chessPieceAtPosition = board.getPiece(myPosition);
        if (chessPieceAtPosition == null) {
            return new HashSet<>();
        }

        Collection<ChessMove> validMoves = new HashSet<>();

        switch (chessPieceAtPosition.getPieceType()) {
            case KING -> getKingMoves(board, myPosition, validMoves);
            case QUEEN -> getQueenMoves(board, myPosition, validMoves);
            case ROOK -> getRookMoves(board, myPosition, validMoves);
            case KNIGHT -> getKnightMoves(board, myPosition, validMoves);
            case BISHOP -> getBishopMoves(board, myPosition, validMoves);
            case PAWN -> getPawnMoves(board, myPosition, validMoves);
//            case KING -> {
//                Integer[][] offsets = {{-1, 1}, {0, 1}, {1, 1},  {-1, 0}, {1, 0},  {-1, -1}, {0, -1}, {1, -1}};
//                getSlidingMoves(board, myPosition, validMoves, offsets);
//            }
        }
        return validMoves;
    }

    private boolean isValidMove(ChessBoard chessBoard, ChessPosition otherPosition) {
        if (chessBoard.isOutOfBounds(otherPosition)) {
            return false;
        }
        if (chessBoard.getPiece(otherPosition) == null) {
            return true;
        }
        return chessBoard.getPiece(otherPosition).getTeamColor() != teamColor;
    }

    private void addPawnMoves(Collection<ChessMove> validMoves, ChessPosition currPosition, ChessPosition newPosition) {
        int WHITE_PAWN_PROMOTION_ROW = 8;
        int BLACK_PAWN_PROMOTION_ROW = 1;
        if (newPosition.getRow() == WHITE_PAWN_PROMOTION_ROW ||
            newPosition.getRow() == BLACK_PAWN_PROMOTION_ROW) {
            validMoves.add(new ChessMove(currPosition, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(currPosition, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(currPosition, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(currPosition, newPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            validMoves.add(new ChessMove(currPosition, newPosition, null));
        }
    }

    private void getPawnMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        int forwardOne = teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;

        // Forward 1
        ChessPosition newPosition = new ChessPosition(currPosition.getRow()+forwardOne, currPosition.getColumn());
        if (!chessBoard.isOutOfBounds(newPosition) && chessBoard.getPiece(newPosition) == null) {
            addPawnMoves(validMoves, currPosition, newPosition);

            // Forward 2
            newPosition = new ChessPosition(newPosition.getRow()+forwardOne, newPosition.getColumn());

            int WHITE_PAWN_HOME_ROW = 2;
            int BLACK_PAWN_HOME_ROW = 7;
            if (((currPosition.getRow() == WHITE_PAWN_HOME_ROW && teamColor == ChessGame.TeamColor.WHITE)
                || (currPosition.getRow() == BLACK_PAWN_HOME_ROW && teamColor == ChessGame.TeamColor.BLACK))
                && chessBoard.getPiece(newPosition) == null) {
                validMoves.add(new ChessMove(currPosition, newPosition, null));
            }
        }

        // Diagonal Left
        newPosition = new ChessPosition(currPosition.getRow()+forwardOne, currPosition.getColumn()-1);
        if (isValidMove(chessBoard, newPosition) && chessBoard.getPiece(newPosition) != null) {
            addPawnMoves(validMoves, currPosition, newPosition);
        }

        // Diagonal Right
        newPosition = new ChessPosition(currPosition.getRow()+forwardOne, currPosition.getColumn()+1);
        if (isValidMove(chessBoard, newPosition) && chessBoard.getPiece(newPosition) != null) {
            addPawnMoves(validMoves, currPosition, newPosition);
        }
    }

    private void getBishopMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        Integer[][] offsets = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}}; // top-right, bottom-right, bottom-left, top-left
        getSlidingMoves(chessBoard, currPosition, validMoves, offsets);
    }

    private void getRookMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        Integer[][] offsets = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
        getSlidingMoves(chessBoard, currPosition, validMoves, offsets);
    }

    private void getQueenMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        getBishopMoves(chessBoard, currPosition, validMoves);
        getRookMoves(chessBoard, currPosition, validMoves);
    }

    private void getSlidingMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves, Integer[][] offsets) {
        int currRow, currColumn;
        ChessPosition newPosition;

        for (Integer[] offset : offsets) {
            currRow = currPosition.getRow();
            currColumn = currPosition.getColumn();
            while (true) {
                currRow += offset[0];
                currColumn += offset[1];
                newPosition = new ChessPosition(currRow, currColumn);
                if (!isValidMove(chessBoard, newPosition)) {
                    break;
                }
                validMoves.add(new ChessMove(currPosition, newPosition, null));
                if (chessBoard.getPiece(newPosition) != null) {
                    break;
                }
            }
        }
    }

    private void getKingMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        Integer[][] offsets = {{-1, 1}, {0, 1}, {1, 1},
            {-1, 0}, {1, 0},
            {-1, -1}, {0, -1}, {1, -1}
        };
        getOffsetMoves(chessBoard, currPosition, validMoves, offsets);

//        if (!hasMoved) {
//            int rookRow = teamColor == ChessGame.TeamColor.WHITE ? 1 : chessBoard.BOARD_SIZE;
//            ChessPiece leftRook = chessBoard.getPiece(new ChessPosition(rookRow, 1));
//            ChessPiece rightRook = chessBoard.getPiece(new ChessPosition(rookRow, 8));
//
//            if (leftRook != null && !leftRook.hasMoved) {
//                // Check for spaces
//                ChessPiece pieceOne = chessBoard.getPiece(new ChessPosition(rookRow, 2));
//                ChessPiece pieceTwo = chessBoard.getPiece(new ChessPosition(rookRow, 3));
//                ChessPiece pieceThree = chessBoard.getPiece(new ChessPosition(rookRow, 4));
//                if (pieceOne == null && pieceTwo == null && pieceThree == null) {
//                    validMoves.add(new ChessMove(currPosition, new ChessPosition(rookRow, 3), null));
//                }
//            }
//
//            if (rightRook != null && !rightRook.hasMoved) {
//                // Check for spaces
//                ChessPiece pieceOne = chessBoard.getPiece(new ChessPosition(rookRow, 6));
//                ChessPiece pieceTwo = chessBoard.getPiece(new ChessPosition(rookRow, 7));
//                if (pieceOne == null && pieceTwo == null) {
//                    validMoves.add(new ChessMove(currPosition, new ChessPosition(rookRow, 7), null));
//                }
//            }
//        }
    }

    private void getKnightMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves) {
        Integer[][] offsets = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
        getOffsetMoves(chessBoard, currPosition, validMoves, offsets);
    }

    private void getOffsetMoves(ChessBoard chessBoard, ChessPosition currPosition, Collection<ChessMove> validMoves, Integer[][] offsets) {
        ChessPosition newPosition;
        for (Integer[] offset : offsets) {
            newPosition = new ChessPosition(currPosition.getRow() + offset[0], currPosition.getColumn() + offset[1]);
            if (isValidMove(chessBoard, newPosition)) {
                validMoves.add(new ChessMove(currPosition, newPosition, null));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
            "pieceColor=" + teamColor +
            ", type=" + type +
            '}';
    }
}
