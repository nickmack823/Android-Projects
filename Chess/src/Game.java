import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    Player p1, p2;
    Board board;

    public Game(Player player1, Player player2) {
        p1 = player1;
        p2 = player2;
    }

    public void playGame() throws InterruptedException {
        board = new Board();

        while (!board.gameOver) {
            p1.board = board;
            p2.board = board;
            int[][] move = null;
            if (board.turn == 0) {
                System.out.println("Player 1 thinking...");
                move = p1.findMove();
                if (move[0] == null) {
                    System.out.println("White has no valid moves. Black wins!");
                    board.printBoard();
                    return;
                }
                String piece = board.getSpacePiece(move[0][0], move[0][1]);
                System.out.println("Player 1 move: " + piece + "(" + move[0][0] + ", " + move[0][1] +
                        ") -> " + "(" + move[1][0] + ", " + move[1][1] + ")");
            } else if (board.turn == 1) {
                System.out.println("Player 2 thinking...");
                move = p2.findMove();
                if (move[0] == null) {
                    System.out.println("Black has no valid moves. White wins!");
                    board.printBoard();
                    return;
                }
                String piece = board.getSpacePiece(move[0][0], move[0][1]);
                System.out.println("Player 2 move: " + piece + "(" + move[0][0] + ", " + move[0][1] +
                        ") -> " + "(" + move[1][0] + ", " + move[1][1] + ")");
            }
            board.makeMove(move[0], move[1]);
            board.printBoard();
            if (board.pieceCaptured.equals("K") || board.pieceCaptured.equals("k")) {
                return;
            }
//            Thread.sleep(3000);
        }
    }

    public static void main(String args[]) {
        Board board = new Board();
        Player randomPlayer = new Player("random");
        board.printBoard();

        Game g1 = new Game(randomPlayer, randomPlayer);
        try {
            g1.playGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
