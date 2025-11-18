package chess;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    final private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public ChessBoard(ChessBoard board) {
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                var position = new ChessPosition(row,col);
                var piece = board.getPiece(position);
                if (piece != null) {
                    this.addPiece(position,new ChessPiece(piece));
                }
            }
        }
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
     * Adds a chess piece to the chessboard (avoiding the ChessPosition/ChessPiece boilerplate)
     *
     * @param row the row to add the piece to
     * @param col the column to add the piece to
     * @param team the team color of the piece
     * @param type the piece type
     */
    private void addPiece(int row, int col, ChessGame.TeamColor team, ChessPiece.PieceType type) {
        board[row-1][col-1] = new ChessPiece(team,type);
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

    private void createPawnRow(int row, ChessGame.TeamColor team) {
        for (int col = 1; col <= 8; col ++) {
            var position = new ChessPosition(row,col);
            var piece = new ChessPiece(team, ChessPiece.PieceType.PAWN);
            addPiece(position,piece);
        }
    }

    private void createBackRow(int row, ChessGame.TeamColor team) {
        addPiece(row,1,team, ChessPiece.PieceType.ROOK);
        addPiece(row,2,team, ChessPiece.PieceType.KNIGHT);
        addPiece(row,3,team, ChessPiece.PieceType.BISHOP);
        addPiece(row,4,team, ChessPiece.PieceType.QUEEN);
        addPiece(row,5,team, ChessPiece.PieceType.KING);
        addPiece(row,6,team, ChessPiece.PieceType.BISHOP);
        addPiece(row,7,team, ChessPiece.PieceType.KNIGHT);
        addPiece(row,8,team, ChessPiece.PieceType.ROOK);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        createBackRow(1,ChessGame.TeamColor.WHITE);
        createPawnRow(2,ChessGame.TeamColor.WHITE);

        createBackRow(8,ChessGame.TeamColor.BLACK);
        createPawnRow(7,ChessGame.TeamColor.BLACK);
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

    private String toStringCommon(boolean asWhite) {
        var uni = "\u001b[";
        var black = "40;";
        var gray = "100;";
        var blue = "34;";
        var text = "39;";
        var back = "49;";
        var light = "37;";
        var end = "1m";

        var ranks = new ArrayList<String>();
        ranks.add(" ");
        ranks.add("a");
        ranks.add("b");
        ranks.add("c");
        ranks.add("d");
        ranks.add("e");
        ranks.add("f");
        ranks.add("g");
        ranks.add("h");
        ranks.add(" ");

        var files = new ArrayList<String>();
        files.add(" ");
        files.add("1");
        files.add("2");
        files.add("3");
        files.add("4");
        files.add("5");
        files.add("6");
        files.add("7");
        files.add("8");
        files.add(" ");


        if (asWhite) {

        }

        StringBuilder out = new StringBuilder();

        if (asWhite) {
            for (int row = 9; row >= 0; row --) {
                for (int col = 0; col <= 9; col ++) {
                    toStringInner(uni, black, gray, blue, text, light, end, ranks, files, out, row, col);
                }
                out.append(uni + back + text + end + "\n");
            }
        } else {
            for (int row = 0; row <= 9; row ++) {
                for (int col = 9; col >= 0; col --) {
                    toStringInner(uni, black, gray, blue, text, light, end, ranks, files, out, row, col);
                }
                out.append(uni + back + text + end + "\n");
            }
        }

        out.append(uni + back + text + "m");

        return out.toString();
    }

    private void toStringInner(String uni, String black, String gray, String blue, String text,
                               String light, String end, ArrayList<String> ranks, ArrayList<String> files,
                               StringBuilder out, int row, int col) {
        if (row == 9 || row == 0 || col == 9 || col == 0) {
            out.append(uni + black + light + end);
        }

        if (row == 0 || row == 9) {
            out.append(" ");
            out.append(ranks.get(col));
            out.append(" ");
            return;
        }

        if (col == 0 || col == 9) {
            out.append(" ");
            out.append(files.get(row));
            out.append(" ");
            return;
        }

        var piece = getPiece(new ChessPosition(row, col));
        out.append(uni);

        if ((col + row) % 2 == 0) {
            out.append(black);
        } else {
            out.append(gray);
        }

        if (piece == null) {
            out.append(end + "   ");
        } else {
            if (piece.color == ChessGame.TeamColor.WHITE) {
                out.append(text);
            } else {
                out.append(blue);
            }
            out.append(end + " " + piece + " ");
        }
    }

    @Override
    public String toString() {
        return toStringCommon(true);
    }

    public String toStringWhite() {
        return toString();
    }

    public String toStringBlack() {
        return toStringCommon(false);
    }
}
