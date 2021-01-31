
package byow.Phase1;

import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private Position upperLeft;
    private Position upperRight;
    private Position lowerLeft;
    private Position lowerRight;
    private int length;
    private int width;
    private int face;
    private String hallwayEdge;
    private int minNeighborDist;
    private Map<Integer, Room> neighbors;
    private boolean connectedTop;
    private boolean connectedBottom;
    private boolean connectedLeft;
    private boolean connectedRight;
    private int edgeTop;
    private int edgeBottom;
    private int edgeLeft;
    private int edgeRight;
    private List<Integer> connections;
    private int hallwayAsRoomHallStart;
    private int hallwayAsRoomHallEnd;

    public Room(Position upperLeft, int width, int length) {
        this.connections = new ArrayList<>();
        this.connectedTop = false;
        this.connectedBottom = false;
        this.connectedLeft = false;
        this.connectedRight = false;
        this.neighbors = new HashMap<>();
        this.minNeighborDist = 0;
        this.hallwayEdge = "";
        this.face = 0;
        this.upperLeft = upperLeft;
        upperRight = new Position(upperLeft.getX() + width,
                upperLeft.getY(), Tileset.WALL);
        lowerLeft = new Position(upperLeft.getX(),
                upperLeft.getY() - length, Tileset.WALL);
        lowerRight = new Position(upperLeft.getX() + width,
                upperLeft.getY() - length, Tileset.WALL);
        this.width = width;
        this.length = length;
    }


    public List<Integer> getConnections() {
        return this.connections;
    }

    public void addConnection(int connection) {
        this.connections.add(connection);
    }

    public List<Position> getPos() {
        List<Position> res = new ArrayList<>();
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == length - 1 || x == 0 || x == width - 1) {
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

    public int getHallwayAsRoomHallEnd() {
        return hallwayAsRoomHallEnd;
    }

    public void setHallwayAsRoomHallEnd(int hallwayAsRoomHallEnd) {
        this.hallwayAsRoomHallEnd = hallwayAsRoomHallEnd;
    }

    public void setHallwayAsRoomHallStart(int hallwayAsRoomHallStart) {
        this.hallwayAsRoomHallStart = hallwayAsRoomHallStart;
    }

    public int getHallwayAsRoomHallStart() {
        return hallwayAsRoomHallStart;
    }

    public Position getUpperLeft() {
        return upperLeft;
    }

    public Position getLowerRight() {
        return lowerRight;
    }

    public Position getLowerLeft() {
        return lowerLeft;
    }

    public Map<Integer, Room> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(int n, Room r) {
        neighbors.put(n, r);
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int newFace) {
        face = newFace;
    }

    public String getHallwayEdge() {
        return hallwayEdge;
    }

    public void setHallwayEdge(String str) {
        hallwayEdge = str;
    }

    public int getMinNeighborDist() {
        return minNeighborDist;
    }

    public void settMinNeighborDist(int dist) {
        minNeighborDist = dist;
    }

    public void connectUp(boolean b) {
        this.connectedTop = b;
    }


    public void connectBottom(boolean b) {
        this.connectedBottom = b;
    }


    public void connectLeft(boolean b) {
        this.connectedLeft = b;
    }


    public void connectRight(boolean b) {
        this.connectedRight = b;
    }

    public boolean isConnectedLeft() {
        return connectedLeft;
    }

    public boolean isConnectedRight() {
        return connectedRight;
    }

    public boolean isConnectedTop() {
        return connectedTop;
    }

    public boolean isConnectedBottom() {
        return connectedBottom;
    }

    public Position getUpperRight() {
        return upperRight;
    }

    public int getEdgeBottom() {
        return edgeBottom;
    }

    public int getEdgeLeft() {
        return edgeLeft;
    }

    public int getEdgeRight() {
        return edgeRight;
    }

    public int getEdgeTop() {
        return edgeTop;
    }

    public void setEdgeBottom(int edgeBottom) {
        this.edgeBottom = edgeBottom;
    }

    public void setEdgeLeft(int edgeLeft) {
        this.edgeLeft = edgeLeft;
    }

    public void setEdgeRight(int edgeRight) {
        this.edgeRight = edgeRight;
    }

    public void setEdgeTop(int edgeTop) {
        this.edgeTop = edgeTop;
    }
}
