package byow.Phase1;

import byow.TileEngine.TETile;

import java.util.Objects;

public class Position {
    private int x;
    private int y;
    private TETile tile;

    public Position(int x, int y, TETile tile) {
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getDist(Position p) {
        int xDiff = p.getX() - this.getX();
        double xDist = Math.pow(xDiff, 2);
        int yDiff = p.getY() - this.getY();
        double yDist = Math.pow(yDiff, 2);
        return Math.pow((xDiff + yDiff), 0.5);
    }

    public TETile getTile() {
        return tile;
    }

    public void setTile(TETile t) {
        this.tile = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return "X: " + getX() + "Y: " + getY();
    }
}
