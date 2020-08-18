package solution;

import java.util.Collections;
import java.util.HashMap;

public class Screen {
    private Intcode mIntcode;
    private HashMap<Position, Integer> mTileIds;

    public Screen(long[] instructionArray) {
        mIntcode = new Intcode(instructionArray, null);
        mTileIds = new HashMap<>();
    }

    public void startUp() {
        while (!mIntcode.isHalt()) {
            int xPos = (int) mIntcode.runUntilOutput();
            int yPos = (int) mIntcode.runUntilOutput();
            int tileId = (int) mIntcode.runUntilOutput();
            mTileIds.put(new Position(xPos, yPos), tileId);
        }
    }

    /**
     * Count the number of a type of tile on the screen.
     */
    public int countTileNumber(int tileId) {
        return Collections.frequency(mTileIds.values(), tileId);
    }
}