package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor currentTeam = TeamColor.WHITE;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(board);
        result = 31 * result + Objects.hashCode(currentTeam);
        return result;
    }

    @Override
    public String toString() {
        return board.toString();
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

        var out = new HashSet<ChessMove>();

        var piece = board.getPiece(startPosition);
        if (piece == null) {
            return out;
        }

        var moves = piece.pieceMoves(board, startPosition);
        for (var move : moves) {
            // Create Hypothetical
            var hypothetical = new ChessBoard(board);

            // Do Move
            hypothetical.addPiece(move.startPosition, null);
            hypothetical.addPiece(move.endPosition, piece);

            // Add Move to our return collection if it doesn't hypothetically put us in check.
            if (!isBoardInCheck(hypothetical,piece.getTeamColor())) {
                out.add(move);
            }
        }

        return out;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piece = board.getPiece(move.startPosition);

        if (!validMoves(move.startPosition).contains(move)) {
            throw new InvalidMoveException();
        }

        if (currentTeam != piece.getTeamColor()) {
            throw new InvalidMoveException();
        }

        // Remove piece from old position
        board.addPiece(move.startPosition, null);

        // Promote if needed
        if (move.promotionPiece != null) {
            board.addPiece(move.endPosition,new ChessPiece(piece.getTeamColor(),move.promotionPiece));
        } else {
            board.addPiece(move.endPosition, piece);
        }

        // Change Polarity (ternary)
        currentTeam = currentTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private ChessPosition getKingPosition(ChessBoard board, TeamColor teamColor) {
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

    private boolean isBoardInCheck(ChessBoard board, TeamColor teamColor) {
        var kingPosition = getKingPosition(board, teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var thisPosition = new ChessPosition(row,col);
                var piece = board.getPiece(thisPosition);
                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                var moves = piece.pieceMoves(board, thisPosition);
                for (var move : moves) {
                    if (move.endPosition.equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isBoardInCheck(board, teamColor);
    }

    /**
     * Determines if the given team has any moves that would get them out of check.
     * (Regardless of whether they're in check).
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private boolean hasNoValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var position = new ChessPosition(row,col);
                var piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isBoardInCheck(board, teamColor)) {
            System.out.println("board is in check");
        }

        return hasNoValidMoves(teamColor) && isBoardInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return hasNoValidMoves(teamColor) && !isBoardInCheck(board, teamColor);
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
