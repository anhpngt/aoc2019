package solution;

import java.util.HashMap;

public class Painter {

    private Intcode mIntcode;
    private Position mCurrentPosition;
    private Direction mCurrentDirection;
    private HashMap<Position, Integer> mPaintedLocations;

    public Painter(long[] instructionArray) {
        mIntcode = new Intcode(instructionArray, null);
        mCurrentPosition = new Position(0, 0);
        mCurrentDirection = Direction.N;
        mPaintedLocations = new HashMap<>();
    }

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
        }
    }

    public long getPaintedPanelCount() {
        return mPaintedLocations.size();
    }
}