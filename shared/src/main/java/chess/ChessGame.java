package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean isGameOver;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        isGameOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceAtStartPosition = board.getPiece(startPosition);
        if (pieceAtStartPosition == null) {
            return new HashSet<>();
        }

        Collection<ChessMove> pieceMoves = pieceAtStartPosition.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        ChessPiece pieceAtEndPosition;
        for (ChessMove pieceMove : pieceMoves) {
            // Do the move
            pieceAtEndPosition = board.getPiece(pieceMove.getEndPosition());
            board.addPiece(pieceMove.getEndPosition(), pieceAtStartPosition);
            board.addPiece(pieceMove.getStartPosition(), null);

            // Validate if check
            if (!isInCheck(pieceAtStartPosition.getTeamColor())) {
                validMoves.add(pieceMove);
            }

            // Undo the move
            board.addPiece(pieceMove.getEndPosition(), pieceAtEndPosition);
            board.addPiece(pieceMove.getStartPosition(), pieceAtStartPosition);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceAtStartPosition = board.getPiece(move.getStartPosition());
        if (pieceAtStartPosition != null && pieceAtStartPosition.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not Your Turn. Curr Turn: " + teamTurn);
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move: " + move);
        }

        if (move.getPromotionPiece() != null) {
            pieceAtStartPosition = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), pieceAtStartPosition);
        board.addPiece(move.getStartPosition(), null);

        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece chessPiece;
        ChessPosition chessPosition;
        ChessPosition kingPosition = getKingPosition(teamColor);
        for (int row = 1; row <= ChessBoard.BOARD_SIZE; row++) {
            for (int col = 1; col <= ChessBoard.BOARD_SIZE; col++) {
                chessPosition = new ChessPosition(row, col);
                chessPiece = board.getPiece(chessPosition);
                if (chessPiece == null || chessPiece.getTeamColor() == teamColor) continue;
                for (ChessMove chessMove : chessPiece.pieceMoves(board, chessPosition)) {
                    if (chessMove.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && teamHasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && teamHasNoValidMoves(teamColor);

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { this.board = board; }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition position;
        ChessPiece piece;
        for (int row = 1; row <= ChessBoard.BOARD_SIZE; row++) {
            for (int col = 1; col <= ChessBoard.BOARD_SIZE; col++) {
                position = new ChessPosition(row, col);
                piece = board.getPiece(position);
                if (piece != null
                    && piece.getTeamColor() == teamColor
                    && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean teamHasNoValidMoves(TeamColor teamColor) {
//        ChessPosition chessPosition;
//        ChessPiece chessPiece;
//        for (int row = 1; row <= ChessBoard.BOARD_SIZE; row++) {
//            for (int col = 1; col <= ChessBoard.BOARD_SIZE; col++) {
//                chessPosition = new ChessPosition(row, col);
//                chessPiece = board.getPiece(chessPosition);
//                if (chessPiece == null || chessPiece.getTeamColor() != teamColor) continue;
//                if (!validMoves(chessPosition).isEmpty()) return false;
//            }
//        }
        return true;
    }
}
