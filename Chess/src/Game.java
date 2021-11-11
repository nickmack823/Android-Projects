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
        ArrayList<int[]> moves = board.getPieceMoves(6, 1, "P");
        for (int i = 0; i < moves.size(); i++) {
            System.out.println(moves.get(i)[0] + " " + moves.get(i)[1]);
        }
    }
}
