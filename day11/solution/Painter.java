package solution;

import java.util.HashMap;

public class Painter {

    private Intcode mIntcode;
    private Position mCurrentPosition;
    private Direction mCurrentDirection;
    private HashMap<Position, Integer> mPaintedLocations;

    // For visualizing message painted
    private int mXMax, mXMin, mYMax, mYMin;

    public Painter(long[] instructionArray) {
        mIntcode = new Intcode(instructionArray, null);
        mCurrentPosition = new Position(0, 0);
        mCurrentDirection = Direction.N;
        mPaintedLocations = new HashMap<>();

        mXMax = mXMin = mYMax = mYMin = 0;
    }

    public void setFirstPanelWhite() {
        mPaintedLocations.put(new Position(0, 0), 1);
    }

    /**
     * Starts painting.
     */
    public void run() {
        while (!mIntcode.isHalt()) {
            // Get input value based on color of current panel
            int currentPositionColor = mPaintedLocations.getOrDefault(mCurrentPosition, 0);
            mIntcode.provideInput(new long[] { Long.valueOf(currentPositionColor) });

            // Run the program and get 2 outputs
            int colorOutput = (int) mIntcode.runUntilOutput();
            int rotateOutput = (int) mIntcode.runUntilOutput();

            // Paint black or white
            if (colorOutput != currentPositionColor) {
                mPaintedLocations.put(mCurrentPosition.clone(), colorOutput);
            }

            // Turn, then move forward
            mCurrentDirection = mCurrentDirection.turn(rotateOutput);
            mCurrentPosition.moveForward(mCurrentDirection);
            setMapRange();
        }
    }

    /**
     * @return number of panels painted at least once.
     */
    public long getPaintedPanelCount() {
        return mPaintedLocations.size();
    }

    /**
     * Visualizes the painted message on console.
     */
    public void printPaintedMessage() {
        int messageWidth = mXMax - mXMin + 1;
        int xOffset = mXMin;

        // Printing top-down
        for (int y = mYMax; y >= mYMin; y--) {
            char[] currentMessageLine = new char[messageWidth];
            for (int x = mXMin; x <= mXMax; x++) {
                currentMessageLine[x + xOffset] = mPaintedLocations.getOrDefault(new Position(x, y), 0) == 1 ? '*'
                        : ' ';
            }
            System.out.println(new String(currentMessageLine));
        }
    }

    /**
     * Checks and widens the border of the painted area for visualization.
     */
    private void setMapRange() {
        mXMax = Math.max(mXMax, mCurrentPosition.x);
        mXMin = Math.min(mXMin, mCurrentPosition.x);
        mYMax = Math.max(mYMax, mCurrentPosition.y);
        mYMin = Math.min(mYMin, mCurrentPosition.y);
    }
}