import java.util.ArrayList;

public class Game {

    Player p1, p2;
    Board board;

    public Game(Player player1, Player player2) {
        p1 = player1;
        p2 = player2;
    }

    private void playGame() {
        board = new Board();
    }


    public static void main(String args[]) {
        Board board = new Board();
        board.printBoard();
        ArrayList<int[]> moves = board.getFullMoveset("white");
//        for (int i = 0; i < moves.size(); i++) {
//            System.out.println(moves.get(i)[0] + " " + moves.get(i)[1]);
//        }
        for (int i = 0; i < board.whiteMoveset.size(); i++) {
            int[] pieceSpace = board.whiteIndices.get(i);
            ArrayList<int[]> pieceMoves = board.whiteMoveset.get(board.whiteIndices.get(i));
            System.out.print("Move space: ");
            System.out.print(pieceSpace[0] + " " + pieceSpace[1]);
            System.out.print(" (Piece: " + board.board[pieceSpace[0]][pieceSpace[1]] + ")");
            System.out.println("\nPossible moves: ");
            for (int j = 0; j < pieceMoves.size(); j++) {
                int[] possibleMove = pieceMoves.get(j);
                System.out.print("(" + possibleMove[0] + ", " + possibleMove[1] + ") ");
            }
            System.out.println();
        }
    }
}
