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

    ChessGame.TeamColor color;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
        this.type = type;
    }

    public ChessPiece(ChessPiece piece) {
        this.color = piece.color;
        this.type = piece.type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        String c = switch (type) {
            case PieceType.KING -> "k";
            case PieceType.QUEEN -> "q";
            case PieceType.BISHOP -> "b";
            case PieceType.KNIGHT -> "n";
            case PieceType.ROOK -> "r";
            case PieceType.PAWN -> "p";
        };

        c = color == ChessGame.TeamColor.WHITE ? c.toUpperCase() : c;

        return c;
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
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private boolean isPositionCapturable(ChessBoard board, ChessPosition position) {
        return position != null && (board.getPiece(position) == null || board.getPiece(position).getTeamColor() != color);
    }

    private boolean isPositionEmpty(ChessBoard board, ChessPosition position) {
        return position != null && board.getPiece(position) == null;
    }

    private boolean isPositionKillable(ChessBoard board, ChessPosition position) {
        return position != null && board.getPiece(position) != null && board.getPiece(position).getTeamColor() != color;
    }

    private Collection<ChessMove> getAdjacentMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        ChessPosition[] positions = {
                position.shift(-1,0),
                position.shift(-1,-1),
                position.shift(0,-1),
                position.shift(1,-1),
                position.shift(1,0),
                position.shift(1,1),
                position.shift(0,1),
                position.shift(-1,1)
        };

        for (var position_to_check : positions) {
            if (isPositionCapturable(board,position_to_check)) {
                set.add(new ChessMove(position,position_to_check,null));
            }
        }

        return set;
    }

    private Collection<ChessMove> getLineMoves(ChessBoard board, ChessPosition position, int x_step, int y_step) {
        var set = new HashSet<ChessMove>();

        var position_to_check = position;
        for (int i = 1; i <= 8; i ++) {

            if (isPositionCapturable(board,position_to_check)) {
                set.add(new ChessMove(position,position_to_check,null));
            }

            if (isPositionEmpty(board,position_to_check) || position.equals(position_to_check)) {
                position_to_check = position.shift(i * x_step,i * y_step);
            } else {
                break;
            }
        }

        return set;
    }

    private Collection<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        set.addAll(getLineMoves(board,position,-1,-1));
        set.addAll(getLineMoves(board,position,1,-1));
        set.addAll(getLineMoves(board,position,-1,1));
        set.addAll(getLineMoves(board,position,1,1));

        return set;
    }

    private Collection<ChessMove> getCardinalMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        set.addAll(getLineMoves(board,position,1,0));
        set.addAll(getLineMoves(board,position,-1,0));
        set.addAll(getLineMoves(board,position,0,1));
        set.addAll(getLineMoves(board,position,0,-1));

        return set;
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition position) {
        return getAdjacentMoves(board,position);
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        set.addAll(getCardinalMoves(board,position));
        set.addAll(getDiagonalMoves(board,position));

        return set;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition position) {
        return getDiagonalMoves(board,position);
    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        ChessPosition[] positions = {
                position.shift(-2,1),
                position.shift(-2,-1),
                position.shift(2,1),
                position.shift(2,-1),
                position.shift(1,-2),
                position.shift(-1,-2),
                position.shift(1,2),
                position.shift(-1,2)
        };

        for (var position_to_check : positions) {
            if (isPositionCapturable(board,position_to_check)) {
                set.add(new ChessMove(position,position_to_check,null));
            }
        }

        return set;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition position) {
        return getCardinalMoves(board,position);
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition position) {
        var set = new HashSet<ChessMove>();

        PieceType[] promos = { null };

        int row_polarity = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean in_starting_row = (position.getRow() == 2 && row_polarity == 1) || (position.getRow() == 7 && row_polarity == -1);

        if ((position.getRow() == 7 && row_polarity == 1) || (position.getRow() == 2 && row_polarity == -1)) {
            promos = new PieceType[]{ PieceType.BISHOP, PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT };
        }

        for (var promo : promos) {
            if (isPositionKillable(board,position.shift(row_polarity,-1))) {
                set.add(new ChessMove(position,position.shift(row_polarity,-1),promo));
            }

            if (isPositionEmpty(board,position.shift(row_polarity,0))) {
                set.add(new ChessMove(position,position.shift(row_polarity,0),promo));

                if (isPositionEmpty(board,position.shift(row_polarity*2,0)) && in_starting_row) {
                    set.add(new ChessMove(position,position.shift(row_polarity*2,0),promo));
                }
            }

            if (isPositionKillable(board,position.shift(row_polarity,1))) {
                set.add(new ChessMove(position,position.shift(row_polarity,1),promo));
            }
        }

        return set;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (getPieceType()) {
            case KING -> getKingMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            case BISHOP -> getBishopMoves(board, myPosition);
            case KNIGHT -> getKnightMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case PAWN -> getPawnMoves(board, myPosition);
        };
    }
}
