package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessMove chessMove = (ChessMove) o;
        boolean start = Objects.equals(startPosition, chessMove.startPosition);
        boolean end = Objects.equals(endPosition, chessMove.endPosition);
        boolean promo = promotionPiece == chessMove.promotionPiece;
        return start && end && promo;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(startPosition);
        result = 31 * result + Objects.hashCode(endPosition);
        result = 31 * result + Objects.hashCode(promotionPiece);
        return result;
    }

    @Override
    public String toString() {
        String name = "NULL";
        if (promotionPiece != null) {
            name = switch (promotionPiece) {
                case ChessPiece.PieceType.KNIGHT -> "KNIGHT";
                case ChessPiece.PieceType.QUEEN -> "QUEEN";
                case ChessPiece.PieceType.BISHOP -> "BISHOP";
                case ChessPiece.PieceType.ROOK -> "ROOK";
                default -> "";
            };
        }

        String from = "FROM: " + startPosition.getRow() + "," + startPosition.getColumn();
        String to = " TO: " + endPosition.getRow() + "," + endPosition.getColumn();
        String promo = " PRO: " + name;
        return from + to + promo;
    }
}
