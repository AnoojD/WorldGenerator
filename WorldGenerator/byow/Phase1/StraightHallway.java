package byow.Phase1;

import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

public class StraightHallway extends Room {
    private int width;
    private int length;
    private Position upperLeft;
    private boolean isVertical;
    private int hallStart;
    private int hallEnd;
    private int faceStart;
    private int faceEnd;

    public StraightHallway(Position upperLeft, int width, int length,
                           int hallStart, int hallEnd, boolean isVertical) {
        super(upperLeft, width, length);
        this.width = width;
        this.length = length;
        this.upperLeft = upperLeft;
        this.isVertical = isVertical;
        this.hallStart = hallStart;
        this.hallEnd = hallEnd;
    }

    public List<Position> getPos() {
        List<Position> res = new ArrayList<>();
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (width == 3 && x == 1 && !isVertical) {
                    res.add(new Position(upperLeft.getX() + x,
                            upperLeft.getY() - y, Tileset.FLOOR));
                } else if (length == 3 && y == 1 && isVertical) {
                    res.add(new Position(upperLeft.getX() + x,
                            upperLeft.getY() - y, Tileset.FLOOR));
                } else if (y == 0 || y == length - 1 || x == 0
                        || x == width - 1) {
                    res.add(new Position(upperLeft.getX() + x,
                            upperLeft.getY() - y, Tileset.WALL));
                } else {
                    res.add(new Position(upperLeft.getX() + x,
                            upperLeft.getY() - y, Tileset.FLOOR));
                }
            }
        }
        return res;
    }

    public int getHallStart() {
        return hallStart;
    }

    public int getFaceEnd() {
        return faceEnd;
    }

    public int getFaceStart() {
        return faceStart;
    }

    public void setFaceEnd(int faceEnd) {
        this.faceEnd = faceEnd;
    }

    public void setFaceStart(int faceStart) {
        this.faceStart = faceStart;
    }

    public int getHallEnd() {
        return hallEnd;
    }
}
