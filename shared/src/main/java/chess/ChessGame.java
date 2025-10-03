package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor current_team = TeamColor.WHITE;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return current_team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        current_team = team;
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row,col);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        var king_position = getKingPosition(teamColor);

        if (king_position == null) return false;

        System.out.println(king_position);

        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var this_position = new ChessPosition(row,col);
                var piece = board.getPiece(this_position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    var moves = piece.pieceMoves(board, this_position);
                    for (var move : moves) {
                        if (move.endPosition.equals(king_position)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team has any moves that would get them out of check.
     * (Regardless of whether they're in check).
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var position = new ChessPosition(row,col);
                var piece = board.getPiece(position);
                var moves = piece.pieceMoves(board,position);

                for (var move : moves) {

                    var hypothetical = new ChessBoard(board);
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
