package solution;

class Position {
    public int x, y;

    /**
     * Coordinates are x-right, y-up.
     */
    public Position(int x_, int y_) {
        this.x = x_;
        this.y = y_;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Position))
            return false;

        Position otherPoint = (Position) other;

        return this.x == otherPoint.x && this.y == otherPoint.y;
    }

    @Override
    public int hashCode() {
        return 31 * this.x + this.y;
    }

    public void moveForward(Direction direction) {
        this.x += direction.dx;
        this.y += direction.dy;
    }

    public Position clone() {
        return new Position(this.x, this.y);
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + "]";
    }
}