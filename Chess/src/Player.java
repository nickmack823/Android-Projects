import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Player {

    String playerType;
    public Board board;

    public Player(String type) {
        playerType = type;
    }

    public int[][] findMove() {
        int[][] move = null;
        if (playerType.equals("random")) {
            PlayerRandom playerRandom = new PlayerRandom();
            move =  playerRandom.findMove();
        }
        return move;
    }

    public class PlayerRandom {

        private int[][] findMove() {
            System.out.println("Randomizing move...");
            Random r = new Random();
            HashMap<int[], ArrayList<int[]>> moveset = board.getFullMoveset();
//            System.out.println(moveset);
            Set<int[]> keys = moveset.keySet();
            ArrayList<int[]> keyArray = new ArrayList<>();
            if (moveset.size() == 0) {
                int[][] result = new int[][]{null, null};
                return result;
            }
            for (int[] key : keys) {
                keyArray.add(key);
            }
            int randomStart = r.nextInt(keyArray.size());
            int[] startSpace = keyArray.get(randomStart);

            ArrayList<int[]> pieceMoves = moveset.get(startSpace);
            System.out.println(pieceMoves);
            int randomTarget = r.nextInt(pieceMoves.size());
            int[] targetSpace = pieceMoves.get(randomTarget);
            int[][] result = new int[][]{startSpace, targetSpace};
            return result;
        }
    }
}
