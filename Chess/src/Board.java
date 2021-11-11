import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {

    String[][] board;
    int turn, winner;
    boolean gameOver;
    ArrayList<String[]> boardHistory;


    public Board() {
        board = new String[][] {
                {"r", "h", "b", "q", "k", "b", "h", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "H", "B", "Q", "K", "B", "H", "R"}
        };
    }

    public void makeMove(int piece, int target) {
        return;
    }

    public ArrayList<int[]> getPieceMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();

        // White pawn
        if (piece.equals("P")) {
            // Pawn base movement
            if (spaceEmpty(row - 1, column)) {
                destinations.add(spacePair(row - 1, column));
            }
            // Pawn first move conditions
            if ((row == 6) && spaceEmpty(row - 2, column)) {
                destinations.add(spacePair(row - 2, column));
            }
            // Pawn capturing conditions
            if ((spaceContainsEnemy(row - 1, column - 1, piece))) {
                destinations.add(spacePair(row - 1, column - 1));
            }
            if ((spaceContainsEnemy(row - 1, column + 1, piece))) {
                destinations.add(spacePair(row - 1, column + 1));
            }
            /* TODO: ADD EN PASSANT */
        }
        // Black pawn
        if (piece.equals("p")) {
            // Pawn first move conditions
            if ((row == 1) && spaceEmpty(row+2, column)) {
                destinations.add(spacePair(row+2, column));
            }
            // Pawn capturing conditions
            if ((spaceContainsEnemy(row+1, column-1, piece))) {
                destinations.add(spacePair(row+1, column-1));
            }
            if ((spaceContainsEnemy(row+1, column+1, piece))) {
                destinations.add(spacePair(row+1, column+1));
            }
        }

        // White and Black Knights
        if ((piece.equals("H")) || piece.equals("h")) {
            if ((!spaceContainsAlly(row-2, column-1, piece)) && !spaceOutOfBounds(row-2, column-1)) {
                destinations.add(spacePair(row - 2, column - 1));
            }
            if ((!spaceContainsAlly(row - 2, column + 1, piece)) && !spaceOutOfBounds(row-2, column+1)){
                destinations.add(spacePair(row - 2, column + 1));
            }
            if ((!spaceContainsAlly(row + 2, column - 1, piece)) && !spaceOutOfBounds(row+2, column-1)){
                destinations.add(spacePair(row + 2, column - 1));
            }
            if ((!spaceContainsAlly(row + 2, column + 1, piece)) && !spaceOutOfBounds(row+2, column+1)){
                destinations.add(spacePair(row + 2, column + 1));
            }
        }



        return destinations;
    }

//    private ArrayList<int[]> getFullMoveset(String piece) {
//
//    }

    private String getSpace(int row, int column) {
        String space = null;
        if (!spaceOutOfBounds(row, column)) {
            space = board[row][column];
        }
        return space;
    }

    private boolean spaceOutOfBounds(int row, int column) {
        if (row > 7 || row < 0 || column > 7 || column < 0) {
            return true;
        }
        return false;
    }

    private boolean spaceEmpty(int row, int column) {
        // Prevent checking impossible spaces
        if (spaceOutOfBounds(row, column)) {
            return false;
        }
        if (getSpace(row, column).equals("-")) {
            return true;
        }
        return false;
    }

    private boolean spaceContainsEnemy(int row, int column, String piece) {
        if (spaceOutOfBounds(row, column)) {
            return false;
        }
        // If current piece is White
        if (Character.isUpperCase(piece.charAt(0))) {
            String otherPiece = getSpace(row, column);
            if (Character.isLowerCase(otherPiece.charAt(0))) {
                return true;
            }
        // If current piece is Black
        } else if (Character.isLowerCase(piece.charAt(0))) {
            String otherPiece = getSpace(row, column);
            if (Character.isUpperCase(otherPiece.charAt(0))) {
                return true;
            }
        }
        return false;
    }

    private boolean spaceContainsAlly(int row, int column, String piece) {
        if (spaceOutOfBounds(row, column)) {
            return false;
        }
        // If current piece is White
        /* TODO: Ensure "-" is not considered uppercase nor lowercase */
        if (Character.isUpperCase(piece.charAt(0))) {
            String otherPiece = getSpace(row, column);
            if (Character.isUpperCase(otherPiece.charAt(0))) {
                return true;
            }
        // If current piece is Black
        } else if (Character.isLowerCase(piece.charAt(0))) {
            String otherPiece = getSpace(row, column);
            if (Character.isLowerCase(otherPiece.charAt(0))) {
                return true;
            }
        }
        return false;
    }

    private int[] spacePair(int row, int column) {
        int[] space = new int[] {row, column};
        return space;
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.print("  ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + "   ");
            }
            System.out.println();
        }
    }

}
