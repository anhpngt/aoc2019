package solution;

/**
 * Note that the direction is y-down (opposite from day 11's).
 */
enum Direction {
    N(0, -1), E(1, 0), S(0, 1), W(-1, 0);

    public final int dx, dy;

    Direction(int dx_, int dy_) {
        this.dx = dx_;
        this.dy = dy_;
    }

    /**
     * `input` of 0 means turning ccw, 1 means cw.
     */
    public Direction turn(int input) {
        int newDirectionOrd;
        switch (input) {
            case 0:
                newDirectionOrd = (this.ordinal() + 4 - 1) % 4;
                break;
            case 1:
                newDirectionOrd = (this.ordinal() + 1) % 4;
                break;
            default:
                throw new Error("Invalid input: " + input);
        }

        return Direction.values()[newDirectionOrd];
    }

}