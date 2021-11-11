import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class Board {

    String[][] board;
    int turn, winner;
    boolean gameOver;
    ArrayList<String[]> boardHistory;
    ArrayList<int[]> whiteIndices, blackIndices;
    HashMap<int[], ArrayList<int[]>> whiteMoveset, blackMoveset;


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
        whiteIndices = new ArrayList<>();
        blackIndices = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                whiteIndices.add(new int[] {i+6, j});
                blackIndices.add(new int[] {i, j});
            }
        }
        whiteMoveset = new HashMap<>();
        blackMoveset = new HashMap<>();
    }

    public void makeMove(int piece, int target) {
        return;
    }

    public ArrayList<int[]> getPieceMoves(int[] currSpace) {
        ArrayList<int[]> destinations = new ArrayList<>();
        int row = currSpace[0];
        int column = currSpace[1];
        String piece = board[row][column];
        // Pawns
        if (piece.equals("P") || piece.equals("p")) {
            destinations.addAll(getPawnMoves(row, column, piece));
        }
        // Knights
        if (piece.equals("H") || piece.equals("h")) {
            destinations.addAll(getKnightMoves(row, column, piece));
        }
        // Bishops
        if (piece.equals("B") || piece.equals("b")) {
            destinations.addAll(getBishopMoves(row, column, piece));
        }
        // Rooks
        if (piece.equals("R") || piece.equals("r")) {
            destinations.addAll(getRookMoves(row, column, piece));
        }
        // Queens
        if (piece.equals("Q") || piece.equals("q")) {
            destinations.addAll(getRookMoves(row, column, piece));
            destinations.addAll(getBishopMoves(row, column, piece));
        }
        // Kings
        if (piece.equals("K") || piece.equals("k")) {
            destinations.addAll(getKingMoves(row, column, piece));
        }
        if (Character.isUpperCase(piece.charAt(0))) {
            whiteMoveset.put(currSpace, destinations);
        } else {
            blackMoveset.put(currSpace, destinations);
        }
        return destinations;
    }

    private ArrayList<int[]> getPawnMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        // White pawn
        if (piece.equals("P")) {
            // Pawn base movement
            if (getSpacePiece(row - 1, column).equals("-")) {
                destinations.add(spacePair(row - 1, column));
            }
            // Pawn first move conditions
            if ((row == 6) && getSpacePiece(row - 1, column).equals("-") && getSpacePiece(row - 2, column).equals("-")) {
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
            // Pawn first move conditions (hasn't moved, empty spaces in front)
            if (row == 1 && getSpacePiece(row + 1, column).equals("-") && getSpacePiece(row + 2, column).equals("-")) {
                destinations.add(spacePair(row+2, column));
            }
            // Pawn capturing conditions
            if (spaceContainsEnemy(row+1, column-1, piece)) {
                destinations.add(spacePair(row+1, column-1));
            }
            if (spaceContainsEnemy(row+1, column+1, piece)) {
                destinations.add(spacePair(row+1, column+1));
            }
        }
        return destinations;
    }

    private ArrayList<int[]> getKnightMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        // Up2-Left1
        if (!spaceContainsAlly(row-2, column-1, piece) && !spaceOutOfBounds(row-2, column-1)) {
            destinations.add(spacePair(row - 2, column - 1));
        }
        // Up2-Right1
        if (!spaceContainsAlly(row - 2, column + 1, piece) && !spaceOutOfBounds(row-2, column+1)){
            destinations.add(spacePair(row - 2, column + 1));
        }
        // Down2-Left1
        if (!spaceContainsAlly(row + 2, column - 1, piece) && !spaceOutOfBounds(row+2, column-1)){
            destinations.add(spacePair(row + 2, column - 1));
        }
        // Down2-Right1
        if (!spaceContainsAlly(row + 2, column + 1, piece) && !spaceOutOfBounds(row+2, column+1)){
            destinations.add(spacePair(row + 2, column + 1));
        }

        // Up1-Left2
        if (!spaceContainsAlly(row - 1, column - 2, piece) && !spaceOutOfBounds(row-1, column-2)) {
            destinations.add(spacePair(row - 1, column - 2));
        }
        // Up1-Right2
        if (!spaceContainsAlly(row - 1, column + 2, piece) && !spaceOutOfBounds(row-1, column+2)){
            destinations.add(spacePair(row - 1, column + 2));
        }
        // Down1-Left2
        if (!spaceContainsAlly(row + 1, column - 2, piece) && !spaceOutOfBounds(row+1, column-2)){
            destinations.add(spacePair(row + 1, column - 2));
        }
        // Down1-Right2
        if (!spaceContainsAlly(row + 1, column + 2, piece) && !spaceOutOfBounds(row+1, column+2)){
            destinations.add(spacePair(row + 1, column + 2));
        }
        return destinations;
    }

    private ArrayList<int[]> getBishopMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        boolean checkDiagUpLeft = true;
        boolean checkDiagUpRight = true;
        boolean checkDiagDownLeft = true;
        boolean checkDiagDownRight = true;
        for (int i = 1; i < board[row].length + 1; i++) {
            if (checkDiagUpLeft) {
                // First, check to the upper left. If the next space is empty/contains an enemy and is not out of bounds, add
                if (!spaceContainsAlly(row - i, column - i, piece) && !spaceOutOfBounds(row - i, column - i)) {
                    destinations.add(spacePair(row - i, column - i));
                }
                // If space contains an enemy or an ally, stop checking left (path obstructed)
                if (spaceContainsEnemy(row - i, column - i, piece) || spaceContainsAlly(row - i, column - i, piece)) {
                    checkDiagUpLeft = false;
                }
            }
            if (checkDiagUpRight) {
                // Next, check to the upper right.
                if (!spaceContainsAlly(row - i, column + i, piece) && !spaceOutOfBounds(row - i, column + i)) {
                    destinations.add(spacePair(row - i, column + i));
                }
                if (spaceContainsEnemy(row - i, column + i, piece) || spaceContainsAlly(row - i, column + i, piece)) {
                    checkDiagUpRight = false;
                }
            }
            if (checkDiagDownLeft) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column - i, piece) && !spaceOutOfBounds(row + i, column - i)) {
                    destinations.add(spacePair(row + i, column - i));
                }
                if (spaceContainsEnemy(row + i, column - i, piece) || spaceContainsAlly(row + i, column - i, piece)) {
                    checkDiagDownLeft = false;
                }
            }
            if (checkDiagDownRight) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column + i, piece) && !spaceOutOfBounds(row + i, column + i)) {
                    destinations.add(spacePair(row + i, column + i));
                }
                if (spaceContainsEnemy(row + i, column + i, piece) || spaceContainsAlly(row + i, column + i, piece)) {
                    checkDiagDownRight = false;
                }
            }
        }
        return destinations;
    }

    private ArrayList<int[]> getRookMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        boolean checkLeft = true;
        boolean checkRight = true;
        boolean checkUp = true;
        boolean checkDown = true;
        // Check for pieces in rook's row
        for (int i = 1; i < board[row].length + 1; i++) {
            if (checkLeft) {
                // First, check to the left. If the next space is empty/contains an enemy and is not out of bounds, add
                if (!spaceContainsAlly(row, column - i, piece) && !spaceOutOfBounds(row, column - i)) {
                    destinations.add(spacePair(row, column - i));
                }
                // If space contains an enemy or an ally, stop checking left (path obstructed)
                if (spaceContainsEnemy(row, column - i, piece) || spaceContainsAlly(row, column - i, piece)) {
                    checkLeft = false;
                }
            }
            if (checkRight) {
                // Next, check to the right.
                if (!spaceContainsAlly(row, column + i, piece) && !spaceOutOfBounds(row, column + i)) {
                    destinations.add(spacePair(row, column + i));
                }
                if (spaceContainsEnemy(row, column + i, piece) || spaceContainsAlly(row, column + i, piece)) {
                    checkRight = false;
                }
            }
            if (checkUp) {
                // Now, check upwards.
                if (!spaceContainsAlly(row - i, column, piece) && !spaceOutOfBounds(row - i, column)) {
                    destinations.add(spacePair(row - i, column));
                }
                if (spaceContainsEnemy(row - i, column, piece) || spaceContainsAlly(row - i, column, piece)) {
                    checkUp = false;
                }
            }
            if (checkDown) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column, piece) && !spaceOutOfBounds(row + i, column)) {
                    destinations.add(spacePair(row + i, column));
                }
                if (spaceContainsEnemy(row + i, column, piece) || spaceContainsAlly(row + i, column, piece)) {
                    checkUp = false;
                }
            }
        }
        return destinations;
    }

    private ArrayList<int[]> getKingMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        // Left
        if (!spaceContainsAlly(row, column - 1, piece) && !spaceOutOfBounds(row, column - 1)
                && !kingInCheck(row, column - 1, piece)) {
            destinations.add(spacePair(row, column - 1));
        }
        // Right
        if (!spaceContainsAlly(row, column + 1, piece) && !spaceOutOfBounds(row, column + 1)
                && !kingInCheck(row, column + 1, piece)) {
            destinations.add(spacePair(row, column + 1));
        }
        // Up
        if (!spaceContainsAlly(row - 1, column, piece) && !spaceOutOfBounds(row - 1, column)
                && !kingInCheck(row - 1, column, piece)) {
            destinations.add(spacePair(row - 1, column));
        }
        // Down
        if (!spaceContainsAlly(row + 1, column, piece) && !spaceOutOfBounds(row + 1, column)
                && !kingInCheck(row + 1, column, piece)) {
            destinations.add(spacePair(row + 1, column));
        }
        // Up-Left
        if (!spaceContainsAlly(row - 1, column - 1, piece) && !spaceOutOfBounds(row - 1, column - 1)
                && !kingInCheck(row - 1, column - 1, piece)) {
            destinations.add(spacePair(row - 1, column - 1));
        }
        // Up-Right
        if (!spaceContainsAlly(row - 1, column + 1, piece) && !spaceOutOfBounds(row - 1, column + 1)
                && !kingInCheck(row - 1, column + 1, piece)) {
            destinations.add(spacePair(row - 1, column + 1));
        }
        // Down-Left
        if (!spaceContainsAlly(row + 1, column - 1, piece) && !spaceOutOfBounds(row - 1, column)
                && !kingInCheck(row + 1, column - 1, piece)) {
            destinations.add(spacePair(row + 1, column - 1));
        }
        // Down-Right
        if (!spaceContainsAlly(row + 1, column + 1, piece) && !spaceOutOfBounds(row + 1, column + 1)
                && !kingInCheck(row + 1, column + 1, piece)) {
            destinations.add(spacePair(row + 1, column + 1));
        }
        return destinations;
    }


    private boolean kingInCheck(int row, int column, String piece) {
        boolean check = false;
        // If King is White
        if (Character.isUpperCase(piece.charAt(0))) {
            // Check each Black piece to see if it would reach the White king at this row and column
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    int[] currSpace = new int[]{i, j};
                    String currPiece = board[i][j];
                    // If piece is ally or space is empty, skip
                    if (Character.isUpperCase(currPiece.charAt(0)) || currPiece.equals("-")) {
                        continue;
                    } else {
                        ArrayList<int[]> currPieceMoves = getPieceMoves(currSpace);
                        for (int k = 0; k < currPieceMoves.size(); k++) {
                            int[] currMove = currPieceMoves.get(k);
                            if (currMove[0] == row && currMove[1] == column) {
                                check = true;
                                return check;
                            }
                        }
                    }
                }
            }
        } else if (Character.isLowerCase(piece.charAt(0))) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    int[] currSpace = new int[]{i, j};
                    String currPiece = board[i][j];
                    // If piece is ally or space is empty, skip
                    if (Character.isLowerCase(currPiece.charAt(0)) || currPiece.equals("-")) {
                        continue;
                    } else {
                        ArrayList<int[]> currPieceMoves = getPieceMoves(currSpace);
                        for (int k = 0; k < currPieceMoves.size(); k++) {
                            int[] currMove = currPieceMoves.get(k);
                            if (currMove[0] == row && currMove[1] == column) {
                                check = true;
                                return check;
                            }
                        }
                    }
                }
            }
        }
        return check;
    }

    public ArrayList<int[]> getFullMoveset(String color) {
        ArrayList<int[]> moveset = new ArrayList<>();
        if (color.equals("white")) {
            whiteMoveset.clear();
            for (int i = 0; i < whiteIndices.size(); i++) {
                int[] currSpace = whiteIndices.get(i);
                int row = currSpace[0];
                int column = currSpace[1];
                String currPiece = board[row][column];
                System.out.println(currPiece);
                moveset.addAll(getPieceMoves(currSpace));
            }
        } else if (color.equals("black")) {
            blackMoveset.clear();
            for (int i = 0; i < blackIndices.size(); i++) {
                int[] currSpace = blackIndices.get(i);
                int row = currSpace[0];
                int column = currSpace[1];
                String currPiece = board[row][column];
                System.out.println(currPiece);
                moveset.addAll(getPieceMoves(currSpace));
            }
        }
        return moveset;
    }

    private String getSpacePiece(int row, int column) {
        String spacePiece = null;
        if (!spaceOutOfBounds(row, column)) {
            spacePiece = board[row][column];
        }
        return spacePiece;
    }

    private boolean spaceOutOfBounds(int row, int column) {
        if (row > 7 || row < 0 || column > 7 || column < 0) {
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
            String otherPiece = getSpacePiece(row, column);
            if (Character.isLowerCase(otherPiece.charAt(0))) {
                return true;
            }
        // If current piece is Black
        } else if (Character.isLowerCase(piece.charAt(0))) {
            String otherPiece = getSpacePiece(row, column);
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
            String otherPiece = getSpacePiece(row, column);
            if (Character.isUpperCase(otherPiece.charAt(0))) {
                return true;
            }
        // If current piece is Black
        } else if (Character.isLowerCase(piece.charAt(0))) {
            String otherPiece = getSpacePiece(row, column);
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
