package solution;

import java.util.Collections;
import java.util.HashMap;

public class Screen {
    // Tile IDs for each element type
    private static final int TILE_ID_EMPTY = 0;
    private static final int TILE_ID_WALL = 1;
    private static final int TILE_ID_BLOCK = 2;
    private static final int TILE_ID_PADDLE = 3;
    private static final int TILE_ID_BALL = 4;

    // Position of game score
    private static final Position SCORE_POSITION = new Position(-1, 0);

    // Input code for joy stick
    private static final int JSTICK_LEFT = -1;
    private static final int JSTICK_NEUTRAL = 0;
    private static final int JSTICK_RIGHT = 1;

    private boolean isGameFinished;
    private Intcode mIntcode;
    private HashMap<Position, Integer> mTileIds;
    private Position paddlePos, ballPos;
    private int mScore;

    public Screen(long[] instructionArray) {
        mIntcode = new Intcode(instructionArray, null);
        mTileIds = new HashMap<>();
        mScore = 0;
        isGameFinished = false; // when there are no block left
    }

    /**
     * Start the game up without playing (memory address 0 of the instruction array
     * is set to 1).
     */
    public void startUp() {
        while (!mIntcode.isHalt()) {
            int xPos = (int) mIntcode.runUntilOutput();
            int yPos = (int) mIntcode.runUntilOutput();
            int value = (int) mIntcode.runUntilOutput();
            this.update(new Position(xPos, yPos), value);
        }
    }

    /**
     * Run and actually play the game (memory address 0 of the instruction array is
     * set to 2).
     */
    public void startUpAndPlay() {
        while (!(mIntcode.isHalt() || isGameFinished)) {
            updateInput();

            int xPos = (int) mIntcode.runUntilOutput();
            int yPos = (int) mIntcode.runUntilOutput();
            int value = (int) mIntcode.runUntilOutput();
            this.update(new Position(xPos, yPos), value);
        }
    }

    /**
     * Prints onto console the whole screen for visualization.
     */
    public void GUI() {
        int xmax = 0, xmin = 0, ymax = 0, ymin = 0;
        for (Position p : mTileIds.keySet()) {
            xmax = Math.max(xmax, p.x);
            xmin = Math.min(xmin, p.x);
            ymax = Math.max(ymax, p.y);
            ymin = Math.min(ymin, p.y);
        }

        // Printing top-down, left-right
        int width = xmax - xmin + 1;
        int xoffset = -xmin;
        for (int y = ymax; y >= ymin; y--) {
            char[] line = new char[width];
            for (int x = xmin; x <= xmax; x++) {
                int tileId = mTileIds.getOrDefault(new Position(x, y), 0);
                char val;
                switch (tileId) {
                    case TILE_ID_EMPTY:
                        val = ' ';
                        break;
                    case TILE_ID_WALL:
                        val = '|';
                        break;
                    case TILE_ID_BLOCK:
                        val = 'x';
                        break;
                    case TILE_ID_PADDLE:
                        val = '-';
                        break;
                    case TILE_ID_BALL:
                        val = 'o';
                        break;
                    default:
                        throw new Error("Invalid tile ID (" + tileId + ")");
                }

                line[x + xoffset] = val;
            }
            System.out.println(new String(line));
        }
    }

    /**
     * @param tileId ID of a type of tile
     * @return the number of a type of tile on the screen.
     */
    public int countTileNumber(int tileId) {
        return Collections.frequency(mTileIds.values(), tileId);
    }

    /**
     * @return current game's score.
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Update tile or score, depending on the input position. Also checks if there
     * are any blocks left thus set the flag to finish the game.
     */
    private void update(Position position, int value) {
        if (position.equals(SCORE_POSITION)) {
            mScore = value;
            if (countTileNumber(TILE_ID_BLOCK) == 0) {
                isGameFinished = true;
            }
        } else {
            mTileIds.put(position.clone(), value);

            // Track position of paddle and ball
            if (value == TILE_ID_PADDLE) {
                paddlePos = position.clone();
            } else if (value == TILE_ID_BALL) {
                ballPos = position.clone();
            }
        }
    }

    /**
     * Update desired input based on relative position between paddle and ball.
     */
    private void updateInput() {
        if (paddlePos == null || ballPos == null) {
            return;
        }
        if (paddlePos.x > ballPos.x) {
            mIntcode.setDefaultInput(JSTICK_LEFT);
        } else if (paddlePos.x == ballPos.x) {
            mIntcode.setDefaultInput(JSTICK_NEUTRAL);
        } else {
            mIntcode.setDefaultInput(JSTICK_RIGHT);
        }
    }
}