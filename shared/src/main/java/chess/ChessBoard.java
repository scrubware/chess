package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    final private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
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

    /*private void addPiece(int row, int col, ChessGame.TeamColor team, ChessPiece.PieceType type) {
        board[row-1][col-1] = new ChessPiece(team,type);
    }*/

    public void removePiece(ChessPosition position) {
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /*private void removePiece(int row, int col) {
        board[row-1][col-1] = null;
    }*/

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

    /*private ChessPiece getPiece(int row, int col) {
        return board[row-1][col-1];
    }*/

    /*private void createPawnRow(int row, ChessGame.TeamColor team) {
        for (int col = 1; col <= 8; col ++) {
            var position = new ChessPosition(row,col);
            var piece = new ChessPiece(team, ChessPiece.PieceType.PAWN);
            addPiece(position,piece);
        }
    }*/

    /*private void createBackRow(int row, ChessGame.TeamColor team) {
        addPiece(row,1,team, ChessPiece.PieceType.ROOK);
        addPiece(row,2,team, ChessPiece.PieceType.KNIGHT);
        addPiece(row,3,team, ChessPiece.PieceType.BISHOP);
        addPiece(row,4,team, ChessPiece.PieceType.QUEEN);
        addPiece(row,5,team, ChessPiece.PieceType.KING);
        addPiece(row,6,team, ChessPiece.PieceType.BISHOP);
        addPiece(row,7,team, ChessPiece.PieceType.KNIGHT);
        addPiece(row,8,team, ChessPiece.PieceType.ROOK);
    }*/

    /*public void createStartingBoard() {
        createBackRow(1,ChessGame.TeamColor.WHITE);
        createPawnRow(2,ChessGame.TeamColor.WHITE);

        createBackRow(8,ChessGame.TeamColor.BLACK);
        createPawnRow(7,ChessGame.TeamColor.BLACK);
    }*/

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col ++) {
                board[row][col] = null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
