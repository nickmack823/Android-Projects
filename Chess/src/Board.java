import javax.print.attribute.standard.JobMediaSheetsSupported;
import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    String[][] board;
    String whitePieces, blackPieces, pieceMoved, pieceCaptured;
    int turn, winner;
    boolean gameOver, whiteInCheck, blackInCheck;
    ArrayList<String[][]> boardHistory;
    ArrayList<String> moveHistory;
    ArrayList<int[]> spaceHistory;
    int[] whiteKingPosition, blackKingPosition, pieceOrigin, pieceTarget;
    ArrayList<int[]> whiteIndices, blackIndices;
    HashMap<int[], ArrayList<int[]>> whiteMoveset, blackMoveset, whitePrevMoveset, blackPrevMoveset;


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
        whitePieces = "KPHBRQ";
        blackPieces = "kphbrq";
        // Indices of each King
        whiteKingPosition = new int[]{7, 4};
        blackKingPosition = new int[]{0, 4};
        whiteInCheck = false;
        blackInCheck = false;
        whiteIndices = new ArrayList<>();
        blackIndices = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                // Starting indices of White and Black pieces
                whiteIndices.add(new int[] {i+6, j});
                blackIndices.add(new int[] {i, j});
            }
        }
        whiteMoveset = new HashMap<>();
        whitePrevMoveset = new HashMap<>();
        blackMoveset = new HashMap<>();
        blackPrevMoveset = new HashMap<>();
        turn = 0;
        winner = -1;
        gameOver = false;
        boardHistory = new ArrayList<>();
        moveHistory = new ArrayList<>();
        spaceHistory = new ArrayList<>();
    }

    public void makeMove(int[] pieceSpace, int[] targetSpace) {
        pieceOrigin = pieceSpace;
        pieceTarget = targetSpace;

        int startRow = pieceSpace[0];
        int startColumn = pieceSpace[1];
        int targetRow = targetSpace[0];
        int targetColumn = targetSpace[1];
        pieceMoved = getSpacePiece(startRow, startColumn);
        pieceCaptured = getSpacePiece(targetRow, targetColumn);

//        System.out.println("MOVING PIECE: " + pieceMoved);
//        System.out.println(targetRow + " " + targetColumn);
        boardHistory.add(board);
        moveHistory.add(pieceMoved + targetRow + targetColumn);
        spaceHistory.add(targetSpace);
        board[startRow][startColumn] = "-";
        board[targetRow][targetColumn] = pieceMoved;

        if (pieceMoved.equals("K")) {
            System.out.println("Updating White King position");
            whiteKingPosition[0] = targetRow;
            whiteKingPosition[1] = targetColumn;
        } else if (pieceMoved.equals("k")) {
            System.out.println("Updating Black King position");
            blackKingPosition[0] = targetRow;
            blackKingPosition[1] = targetColumn;
        }

        if (turn == 0) {
            removeFromIndices("white", pieceSpace);
            addToIndices("white", targetSpace);
//            System.out.println(whiteIndices.size());
        } else if (turn == 1) {
            removeFromIndices("black", pieceSpace);
            addToIndices("black", targetSpace);
//            System.out.println(blackIndices.size());
        }

        if (!pieceCaptured.equals("-")) {
            System.out.println(pieceCaptured + " IS BEING CAPTURED.");
            // White capturing
            if (blackPieces.contains(pieceCaptured)) {
                removeFromIndices("black", targetSpace);
            } else if (whitePieces.contains(pieceCaptured)) {
                removeFromIndices("white", targetSpace);
            }
        }
        whiteInCheck = kingInCheck("white");
        blackInCheck = kingInCheck("black");
        turn = Math.abs(turn - 1);
    }

    public void undoMove() {
        int startRow = pieceOrigin[0];
        int startColumn = pieceOrigin[1];
        int targetRow = pieceTarget[0];
        int targetColumn = pieceTarget[1];
        boardHistory.remove(board);
        moveHistory.remove(pieceMoved + targetRow + targetColumn);
        spaceHistory.remove(pieceTarget);
        board[startRow][startColumn] = pieceMoved;
        board[targetRow][targetColumn] = pieceCaptured;
        if (!pieceCaptured.equals("-")) {
            // Replace captured piece
            if (blackPieces.contains(pieceCaptured)) {
                addToIndices("black", pieceTarget);
            } else if (whitePieces.contains(pieceCaptured)) {
                addToIndices("white", pieceTarget);
            }
        }
        if (pieceMoved.equals("K")) {
            whiteKingPosition[0] = startRow;
            whiteKingPosition[1] = startColumn;
        } else if (pieceMoved.equals("k")) {
            blackKingPosition[0] = startRow;
            blackKingPosition[1] = startColumn;
        }
        if (whitePieces.contains(pieceMoved)) {
            removeFromIndices("white", pieceTarget);
            addToIndices("white", pieceOrigin);
        } else if (blackPieces.contains(pieceMoved)) {
            removeFromIndices("black", pieceTarget);
            addToIndices("black", pieceOrigin);
        }
        whiteInCheck = kingInCheck("white");
        blackInCheck = kingInCheck("black");
        turn = Math.abs(turn - 1);
    }

    public boolean kingInCheck(String color) {
        boolean check = false;
        // If King is White
        if (color.equals("white")) {
            // Check each Black piece to see if it would reach the White king at this row and column
            check = isKingInMoveset(blackMoveset, whiteKingPosition[0], whiteKingPosition[1]);
        } else if (color.equals("black")) {
            check = isKingInMoveset(whiteMoveset, blackKingPosition[0], blackKingPosition[1]);
        }
        return check;
    }

    private boolean isKingInMoveset(HashMap<int[], ArrayList<int[]>> moveset, int kingRow, int kingColumn) {
        for (int[] piece : moveset.keySet()) {
            ArrayList<int[]> currPieceMoves = moveset.get(piece);
            for (int k = 0; k < currPieceMoves.size(); k++) {
                int[] currMove = currPieceMoves.get(k);
                if (currMove[0] == kingRow && currMove[1] == kingColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<int[]> getPieceMoves(int[] currSpace) {
        ArrayList<int[]> destinations = new ArrayList<>();
        int row = currSpace[0];
        int column = currSpace[1];
        String piece = board[row][column];
        System.out.println("Getting moves for piece: " + piece + " at " + row + ", " + column);
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
        // Remove moves that result in player's King being in danger at the end of it
        if (destinations.size() != 0) {
            ArrayList<int[]> toRemove = new ArrayList<>();
            for (int[] target : destinations) {
                makeMove(currSpace, target);
                boolean movePutsKingInCheck = false;
                if (turn == 0) {
                    movePutsKingInCheck = blackInCheck;
                } else if (turn == 1) {
                    movePutsKingInCheck = whiteInCheck;
                }
                if (movePutsKingInCheck) {
                    toRemove.add(target);
                }
                undoMove();
            }
            destinations.removeAll(toRemove);
        }
        return destinations;
    }

    private ArrayList<int[]> getPawnMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        // White pawn
        if (piece.equals("P")) {
            // Pawn base movement
            if (spaceEmpty(row - 1, column)) {
                destinations.add(spacePair(row - 1, column));
            }
            // Pawn first move conditions
            if ((row == 6) && spaceEmpty(row - 1, column) && spaceEmpty(row - 2, column)) {
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
            // Pawn base movement
            if (spaceEmpty(row + 1, column)) {
                destinations.add(spacePair(row + 1, column));
            }
            // Pawn first move conditions (hasn't moved, empty spaces in front)
            if (row == 1 && spaceEmpty(row + 1, column) && spaceEmpty(row + 2, column)) {
                destinations.add(spacePair(row + 2, column));
            }
            // Pawn capturing conditions
            if (spaceContainsEnemy(row + 1, column - 1, piece)) {
                destinations.add(spacePair(row + 1, column - 1));
            }
            if (spaceContainsEnemy(row + 1, column + 1, piece)) {
                destinations.add(spacePair(row + 1, column + 1));
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
//        for (int[] d : destinations) {
//            System.out.println(d[0] + ", " + d[1]);
//        }
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
                if (spaceContainsEnemy(row - i, column - i, piece) || spaceContainsAlly(row - i, column - i, piece)
                        || spaceOutOfBounds(row - i, column - i)) {
                    checkDiagUpLeft = false;
                }
            }
            if (checkDiagUpRight) {
                // Next, check to the upper right.
                if (!spaceContainsAlly(row - i, column + i, piece) && !spaceOutOfBounds(row - i, column + i)) {
                    destinations.add(spacePair(row - i, column + i));
                }
                if (spaceContainsEnemy(row - i, column + i, piece) || spaceContainsAlly(row - i, column + i, piece)
                    || spaceOutOfBounds(row - i, column + i)) {
                    checkDiagUpRight = false;
                }
            }
            if (checkDiagDownLeft) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column - i, piece) && !spaceOutOfBounds(row + i, column - i)) {
                    destinations.add(spacePair(row + i, column - i));
                }
                if (spaceContainsEnemy(row + i, column - i, piece) || spaceContainsAlly(row + i, column - i, piece)
                        || spaceOutOfBounds(row + i, column - i)) {
                    checkDiagDownLeft = false;
                }
            }
            if (checkDiagDownRight) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column + i, piece) && !spaceOutOfBounds(row + i, column + i)) {
                    destinations.add(spacePair(row + i, column + i));
                }
                if (spaceContainsEnemy(row + i, column + i, piece) || spaceContainsAlly(row + i, column + i, piece)
                        || spaceOutOfBounds(row + i, column + i)) {
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
                if (spaceContainsEnemy(row, column - i, piece) || spaceContainsAlly(row, column - i, piece)
                        || spaceOutOfBounds(row, column - i)) {
                    checkLeft = false;
                }
            }
            if (checkRight) {
                // Next, check to the right.
                if (!spaceContainsAlly(row, column + i, piece) && !spaceOutOfBounds(row, column + i)) {
                    destinations.add(spacePair(row, column + i));
                }
                if (spaceContainsEnemy(row, column + i, piece) || spaceContainsAlly(row, column + i, piece)
                        || spaceOutOfBounds(row, column + i)) {
                    checkRight = false;
                }
            }
            if (checkUp) {
                // Now, check upwards.
                if (!spaceContainsAlly(row - i, column, piece) && !spaceOutOfBounds(row - i, column)) {
                    destinations.add(spacePair(row - i, column));
                }
                if (spaceContainsEnemy(row - i, column, piece) || spaceContainsAlly(row - i, column, piece)
                        || spaceOutOfBounds(row - i, column)) {
                    checkUp = false;
                }
            }
            if (checkDown) {
                // Now, check upwards.
                if (!spaceContainsAlly(row + i, column, piece) && !spaceOutOfBounds(row + i, column)) {
                    destinations.add(spacePair(row + i, column));
                }
                if (spaceContainsEnemy(row + i, column, piece) || spaceContainsAlly(row + i, column, piece)
                        || spaceOutOfBounds(row + i, column)) {
                    checkDown = false;
                }
            }
        }
        return destinations;
    }

    private ArrayList<int[]> getKingMoves(int row, int column, String piece) {
        ArrayList<int[]> destinations = new ArrayList<>();
        // Left
        if (!spaceContainsAlly(row, column - 1, piece) && !spaceOutOfBounds(row, column - 1)) {
            destinations.add(spacePair(row, column - 1));
        }
        // Right
        if (!spaceContainsAlly(row, column + 1, piece) && !spaceOutOfBounds(row, column + 1)) {
            destinations.add(spacePair(row, column + 1));
        }
        // Up
        if (!spaceContainsAlly(row - 1, column, piece) && !spaceOutOfBounds(row - 1, column)) {
            destinations.add(spacePair(row - 1, column));
        }
        // Down
        if (!spaceContainsAlly(row + 1, column, piece) && !spaceOutOfBounds(row + 1, column)) {
            destinations.add(spacePair(row + 1, column));
        }
        // Up-Left
        if (!spaceContainsAlly(row - 1, column - 1, piece) && !spaceOutOfBounds(row - 1, column - 1)) {
            destinations.add(spacePair(row - 1, column - 1));
        }
        // Up-Right
        if (!spaceContainsAlly(row - 1, column + 1, piece) && !spaceOutOfBounds(row - 1, column + 1)) {
            destinations.add(spacePair(row - 1, column + 1));
        }
        // Down-Left
        if (!spaceContainsAlly(row + 1, column - 1, piece) && !spaceOutOfBounds(row + 1, column - 1)) {
            destinations.add(spacePair(row + 1, column - 1));
        }
        // Down-Right
        if (!spaceContainsAlly(row + 1, column + 1, piece) && !spaceOutOfBounds(row + 1, column + 1)) {
            destinations.add(spacePair(row + 1, column + 1));
        }
        return destinations;
    }

    public HashMap<int[], ArrayList<int[]>> getFullMoveset() {
        if (turn == 0) {
            System.out.println("Getting White moveset...");
            whitePrevMoveset.putAll(whiteMoveset);
            whiteMoveset.clear();
            // Gets moves for each piece White has
            for (int i = 0; i < whiteIndices.size(); i++) {
                int[] currSpace = whiteIndices.get(i);
//                System.out.println("White pieces left: " + whiteIndices.size());
//                System.out.println("Current Space: " + currSpace[0] + ", " + currSpace[1]);
                ArrayList<int[]> pieceMoves = getPieceMoves(currSpace);
                if (pieceMoves.size() != 0) {
                    whiteMoveset.put(currSpace, pieceMoves);
                }
            }
//            System.out.println(whiteKingPosition[0] + ", " + whiteKingPosition[1]);
//            printMoveset(whiteMoveset);
            return whiteMoveset;
        } else if (turn == 1) {
            System.out.println("Getting Black moveset...");
            blackPrevMoveset.putAll(blackMoveset);
            blackMoveset.clear();
            // Gets moves for each piece Black has
            for (int i = 0; i < blackIndices.size(); i++) {
                int[] currSpace = blackIndices.get(i);
//                System.out.println("Black pieces left: " + blackIndices.size());
//                System.out.println("Current Space: " + currSpace[0] + ", " + currSpace[1]);
                ArrayList<int[]> pieceMoves = getPieceMoves(currSpace);
                if (pieceMoves.size() != 0) {
                    blackMoveset.put(currSpace, pieceMoves);
                }
            }
//            System.out.println(blackKingPosition[0] + ", " + blackKingPosition[1]);
            printMoveset(blackMoveset);
            return blackMoveset;
        }
        return null;
    }

    private boolean spaceOutOfBounds(int row, int column) {
        if (row > 7 || row < 0 || column > 7 || column < 0) {
            return true;
        }
        return false;
    }

    private boolean spaceEmpty(int row, int column) {
        boolean empty = false;
        if (spaceOutOfBounds(row, column)) {
            return empty;
        }
        if (getSpacePiece(row, column).equals("-")) {
            empty = true;
        }
        return empty;
    }

    public String getSpacePiece(int row, int column) {
        String spacePiece = null;
        if (!spaceOutOfBounds(row, column)) {
            spacePiece = board[row][column];
        }
        return spacePiece;
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

    private void addToIndices(String indices, int[] space) {
        if (indicesContainsSpace(indices, space)) {
            return;
        } else if (indices.equals("white")) {
            whiteIndices.add(space);
        } else if (indices.equals("black")) {
            blackIndices.add(space);
        }
    }

    private void removeFromIndices(String indices, int[] space) {
        int removeRow = space[0];
        int removeColumn = space[1];
        if (indices.equals("white")) {
            for (int[] s : whiteIndices) {
                if (s[0] == removeRow && s[1] == removeColumn) {
                    whiteIndices.remove(s);
//                    if (s[0] == whiteKingPosition[0] && s[1] == whiteKingPosition[1]) {
//                        System.out.println("REMOVING WHITE KING");
//                    }
                    return;
                }
            }
        } else if (indices.equals("black")) {
            for (int[] s : blackIndices) {
                if (s[0] == removeRow && s[1] == removeColumn) {
                    blackIndices.remove(s);
//                    if (s[0] == blackKingPosition[0] && s[1] == blackKingPosition[1]) {
//                        System.out.println("REMOVING BLACK KING");
//                    }
                    return;
                }
            }
        }
    }

    private boolean indicesContainsSpace(String indices, int[] space) {
        boolean contains = false;
        if (indices.equals("white")) {
            for (int[] s : whiteIndices) {
                if (s[0] == space[0] && s[1] == space[1]) {
                    contains = true;
                    break;
                }
            }
        } else if (indices.equals("black")) {
            for (int[] s : blackIndices) {
                if (s[0] == space[0] && s[1] == space[1]) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
    private void printMoveset(HashMap<int[], ArrayList<int[]>> moveset) {
        System.out.print("{ ");
        for (int[] key : moveset.keySet()) {
            System.out.print("(" + key[0] + ", " + key[1] + ")" + ": ");
            for (int[] pos : moveset.get(key)) {
                System.out.print("(" + pos[0] + ", " + pos[1] + "), ");
            }
        }
        System.out.print("}");
    }

    public void printBoard() {
        System.out.println(moveHistory);
        System.out.println("     0   1   2   3   4   5   6   7");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    System.out.print(i + "    ");
                }
                String recentMove = null;
                int row = -1;
                int column = -1;
                if (moveHistory.size() != 0) {
                    recentMove = moveHistory.get(moveHistory.size() - 1);
                    recentMove = recentMove.substring(1);
                    row = Integer.parseInt(String.valueOf(recentMove.charAt(0)));
                    column = Integer.parseInt(String.valueOf(recentMove.charAt(1)));
                }
                if (recentMove != null) {
                    if (i == row && j == column) {
                        if (turn == 1) {
                            System.out.print("\033[0;92m" + board[i][j] + "\033[0m" + "   ");
                        } else {
                            System.out.print("\033[0;91m" + board[i][j] + "\033[0m" + "   ");
                        }
                    } else {
                        System.out.print(board[i][j] + "   ");
                    }
                } else {
                    System.out.print(board[i][j] + "   ");
                }

            }
            System.out.println();
        }
    }

}
