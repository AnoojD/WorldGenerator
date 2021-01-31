package byow.Phase1;
import byow.Core.RandomUtils;
import byow.TileEngine.Tileset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tesselation {
    private final List<Room> rooms;
    private final List<StraightHallway> hallways;
    private final int worldWidth;
    private final int worldLength;
    private final Random random;

    public Tesselation(Long seed, int worldWidth, int worldLength) {
        this.worldWidth = worldWidth; this.worldLength = worldLength;
        rooms = new ArrayList<>(); hallways = new ArrayList<>();
        random = new Random(seed);
        int numRooms = RandomUtils.uniform(random, 14, 24);
        generateRooms(numRooms);
        createHallwayConnections(); Graph g1 = new Graph(rooms.size());
        for (Room r : rooms) {
            List<Integer> connections = r.getConnections();
            for (int i = 0; i < connections.size(); i++) {
                g1.addEdge(rooms.indexOf(r), connections.get(i));
            }
        }
        g1.printSCCs(); List<List<Integer>> sccs = g1.getScc();
        int count = sccs.size(); int timer = 0;
        while (count > 1 && timer < 4) {
            List<List<Room>> lstRooms = makeListOfList(sccs);
            StraightHallway hallwayConnect = null;
            List<StraightHallway> tempHallways = new ArrayList<>();
            for (StraightHallway h : hallways) {
                int n1 = h.getHallStart(); int n2 = h.getHallEnd();
                int island1 = whichIsland(rooms.get(n1), lstRooms);
                int island2 = whichIsland(rooms.get(n2), lstRooms);
                List<Room> searchRooms = searchRooms(n1, n2, rooms, lstRooms);
                if (sccs.get(0).contains(n1) && sccs.get(0).contains(n2)) {
                    for (int i = 0; i < 4; i++) {
                        Room sccConnect = nearestNeighbor(h, searchRooms, i);
                        int island3 = whichIsland(sccConnect, lstRooms);
                        if (sccConnect != null && island3
                                != island1 && island3 != island2) {
                            hallwayConnect = genHallway(h, sccConnect,
                                    n1, rooms.indexOf(sccConnect), i);
                            if (hallwayConnect != null) {
                                sccConnect.addConnection(n1);
                                rooms.get(n1).addConnection(
                                        rooms.indexOf(sccConnect));
                            }
                        }
                    }
                }
                if (hallwayConnect != null) {
                    tempHallways.add(hallwayConnect);
                }
            }
            hallways.addAll(tempHallways);
            List<Room> searchHallways = convertHallWayToRoom(hallways);
            tempHallways = new ArrayList<>();
            for (Room r : rooms) {
                for (int i = 0; i < 4; i++) {
                    Room sccConnect = nearestNeighbor(r, searchHallways, i);
                    int island3 = whichIsland(r, lstRooms);
                    if (sccConnect != null && island3 != whichIsland(
                            rooms.get(sccConnect.getHallwayAsRoomHallStart()), lstRooms)
                            && island3 != whichIsland(
                                    rooms.get(sccConnect.getHallwayAsRoomHallEnd()), lstRooms)) {
                        hallwayConnect = genHallway(r, sccConnect,
                                rooms.indexOf(r), sccConnect.getHallwayAsRoomHallEnd(), i);
                        if (hallwayConnect != null) {
                            r.addConnection(sccConnect.getHallwayAsRoomHallEnd());
                            rooms.get(
                                    sccConnect.getHallwayAsRoomHallEnd()).addConnection(
                                            rooms.indexOf(r));
                        }
                    }
                }
                if (hallwayConnect != null) {
                    tempHallways.add(hallwayConnect);
                }
            }
            hallways.addAll(tempHallways); Graph g2 = new Graph(rooms.size());
            for (Room r : rooms) {
                List<Integer> connections = r.getConnections();
                for (int i = 0; i < connections.size(); i++) {
                    g2.addEdge(rooms.indexOf(r), connections.get(i));
                }
            }
            g2.printSCCs(); sccs = g2.getScc(); count = sccs.size(); timer++;
            connectIslands(sccs);
        }
    }

    public void connectIslands(List<List<Integer>> sccs) {
        List<Integer> min = null; int minLen = 1000;
        for (List<Integer> lst : sccs) {
            int size = lst.size();
            if (size < minLen) {
                minLen = size; min = lst;
            }
        }
        if (minLen == 1) {
            Room refRoom = rooms.get(min.get(0));
            boolean matchFound = false; int timer2 = 0;
            List<Room> tempRooms = new ArrayList<>();
            for (Room room : rooms) {
                if (!room.equals(refRoom)) {
                    tempRooms.add(room);
                }
            }
            while (!matchFound && timer2 < 3) {
                boolean innerMatch = false; int i = 0;
                int countRoom = tempRooms.size();
                while (!innerMatch && i <= 3 && countRoom > 2) {
                    Room sccConnect = nearestNeighbor(refRoom, tempRooms, i);
                    if (sccConnect != null) {
                        StraightHallway hallwayConnect = genHallway(refRoom, sccConnect,
                                rooms.indexOf(refRoom),
                                sccConnect.getHallwayAsRoomHallEnd(), i);
                        if (hallwayConnect == null) {
                            tempRooms.remove(sccConnect);
                        } else {
                            refRoom.addConnection(rooms.indexOf(sccConnect));
                            sccConnect.addConnection(rooms.indexOf(refRoom));
                            hallways.add(hallwayConnect); matchFound = true; innerMatch = true;
                        }
                    }
                    i = i + 1;
                }
                timer2++;
            }
        }
    }


    public List<List<Room>> makeListOfList(List<List<Integer>> sccs) {
        List<List<Room>> lstRooms = new ArrayList<>();
        for (int j = 0; j < sccs.size(); j++) {
            List<Room> temp = new ArrayList<>();
            for (Room r : rooms) {
                int rIndex = rooms.indexOf(r);
                if (sccs.get(j).contains(rIndex)) {
                    temp.add(r);
                }
            }
            lstRooms.add(temp);
        }
        return lstRooms;
    }


    public void createHallwayConnections() {
        for (Room r : rooms) {
            for (int i = 0; i < 4; i++) {
                boolean connected = false;
                switch (i) {
                    case (0):
                        connected = r.isConnectedTop();
                        break;
                    case (1):
                        connected = r.isConnectedBottom();
                        break;
                    case (2):
                        connected = r.isConnectedLeft();
                        break;
                    case (3):
                        connected = r.isConnectedRight();
                        break;
                    default:
                        break;
                }
                if (!connected) {
                    Room neighbor = nearestNeighbor(r, rooms, i);
                    if (neighbor != null) {
                        StraightHallway hall = genHallway(r,
                                neighbor, rooms.indexOf(r),
                                rooms.indexOf(neighbor), i);
                        if (hall != null) {
                            if (i == 0) {
                                r.connectUp(true);
                                neighbor.connectBottom(true);
                                r.setEdgeTop(rooms.indexOf(neighbor));
                                neighbor.setEdgeBottom(rooms.indexOf(r));
                                hall.setFaceStart(0); hall.setFaceEnd(1);
                                r.addConnection(rooms.indexOf(neighbor));
                                neighbor.addConnection(rooms.indexOf(r));
                            } else if (i == 1) {
                                r.connectBottom(true);
                                neighbor.connectUp(true);
                                r.setEdgeBottom(rooms.indexOf(neighbor));
                                neighbor.setEdgeTop(rooms.indexOf(r));
                                hall.setFaceStart(1); hall.setFaceEnd(0);
                                r.addConnection(rooms.indexOf(neighbor));
                                neighbor.addConnection(rooms.indexOf(r));
                            } else if (i == 2) {
                                r.connectLeft(true);
                                neighbor.connectRight(true);
                                r.setEdgeLeft(rooms.indexOf(neighbor));
                                neighbor.setEdgeRight(rooms.indexOf(r));
                                hall.setFaceStart(2); hall.setFaceEnd(3);
                                r.addConnection(rooms.indexOf(neighbor));
                                neighbor.addConnection(rooms.indexOf(r));
                            } else {
                                r.connectRight(true);
                                neighbor.connectLeft(true);
                                r.setEdgeRight(rooms.indexOf(neighbor));
                                neighbor.setEdgeLeft(rooms.indexOf(r));
                                hall.setFaceStart(3); hall.setFaceEnd(2);
                                r.addConnection(rooms.indexOf(neighbor));
                                neighbor.addConnection(rooms.indexOf(r));
                            }
                            hallways.add(hall);
                        }
                    }
                }
            }
        }
    }


    public void generateRooms(int numberOfRooms) {
        while (numberOfRooms != 0) {
            Room randRoom = genRandRoom();
            while (!isValidRoom(randRoom, worldWidth, worldLength)) {
                randRoom = genRandRoom();
            }
            while (roomsOverlap(randRoom, rooms)) {
                randRoom = genRandRoom();
                while (!isValidRoom(randRoom, worldWidth, worldLength)) {
                    randRoom = genRandRoom();
                }
            }
            rooms.add(randRoom);
            numberOfRooms--;
        }
    }

    public List<Room> searchRooms(int n1, int n2, List<Room> room,
                                  List<List<Room>> islandRooms) {
        List<Room> res = new ArrayList<>();
        int islandN1 = whichIsland(room.get(n1), islandRooms);
        int islandN2 = whichIsland(room.get(n2), islandRooms);
        for (Room r : room) {
            int islandR = whichIsland(r, islandRooms);
            if (islandR != islandN1 && islandR != islandN2) {
                res.add(r);
            }
        }
        return res;
    }

    public int whichIsland(Room r, List<List<Room>> allIslands) {
        for (List<Room> lst : allIslands) {
            if (lst.contains(r)) {
                return allIslands.indexOf(lst);
            }
        }
        return -1;
    }

    public List<Room> convertHallWayToRoom(List<StraightHallway> sh) {
        List<Room> hallwaysAsRoom = new ArrayList<>();
        for (StraightHallway h: sh) {
            Room t = h;
            t.setHallwayAsRoomHallStart(h.getHallStart());
            t.setHallwayAsRoomHallEnd(h.getHallEnd());
            hallwaysAsRoom.add(t);
        }
        return hallwaysAsRoom;
    }

    public StraightHallway case0(Room r, Room neighbor, int i, int j) {
        if (r.getHallwayEdge().equals("Left")) {
            return new StraightHallway(new Position(r.getUpperLeft().getX(),
                    neighbor.getLowerRight().getY() +  1, Tileset.WALL),
                    3, neighbor.getLowerRight().getY()
                    - r.getUpperLeft().getY() + 2,
                    i, j, false);
        } else if (r.getHallwayEdge().equals("Right")) {
            return new StraightHallway(
                    new Position(r.getUpperRight().getX() - 3,
                            neighbor.getLowerRight().getY() + 1, Tileset.WALL),
                    3, neighbor.getLowerRight().getY()
                    - r.getUpperLeft().getY() + 2,
                    i, j, false);
        } else {
            if (neighbor.getWidth() > r.getWidth()) {
                int mid = ((r.getUpperLeft().getX()
                        + r.getUpperRight().getX()) / 2) - 1;
                return new StraightHallway(new Position(mid,
                        neighbor.getLowerRight().getY() + 1, Tileset.WALL),
                        3, neighbor.getLowerRight().getY()
                        - r.getUpperLeft().getY() + 2,
                        i, j, false);
            } else {
                int neighborMid = ((neighbor.getUpperLeft().getX()
                        + neighbor.getUpperRight().getX()) / 2) - 1;
                return new StraightHallway(new Position(neighborMid,
                        neighbor.getLowerRight().getY() + 1, Tileset.WALL),
                        3, neighbor.getLowerRight().getY()
                        - r.getUpperLeft().getY() + 2,
                        i, j, false);
            }
        }
    }

    public StraightHallway case1(Room r, Room neighbor, int i, int j) {
        if (r.getHallwayEdge().equals("Left")) {
            return new StraightHallway(
                    new Position(r.getLowerLeft().getX(),
                            r.getLowerRight().getY() + 1, Tileset.WALL), 3,
                    r.getLowerLeft().getY()
                            - neighbor.getUpperRight().getY() + 2,
                    i, j, false);
        } else if (r.getHallwayEdge().equals("Right")) {
            return new StraightHallway(
                    new Position(r.getLowerRight().getX() - 3,
                            r.getLowerRight().getY() + 1, Tileset.WALL), 3,
                    r.getLowerLeft().getY()
                            - neighbor.getUpperRight().getY() + 2,
                    i, j, false);
        } else {
            if (neighbor.getWidth() > r.getWidth()) {
                int mid = ((r.getLowerLeft().getX()
                        + r.getLowerRight().getX()) / 2) - 1;
                return new StraightHallway(
                        new Position(mid, r.getLowerRight().getY()
                                + 1, Tileset.WALL), 3,
                        r.getLowerLeft().getY()
                                - neighbor.getUpperRight().getY() + 2,
                        i, j, false);
            } else {
                int neighborMid = ((neighbor.getUpperLeft().getX()
                        + neighbor.getUpperRight().getX()) / 2) - 1;
                return new StraightHallway(
                        new Position(neighborMid,
                                r.getLowerRight().getY() + 1,
                                Tileset.WALL), 3,
                        r.getLowerLeft().getY()
                                - neighbor.getUpperRight().getY() + 2,
                        i, j, false);
            }
        }
    }

    public StraightHallway case2(Room r, Room neighbor, int i, int j) {
        if (r.getHallwayEdge().equals("Top")) {
            return new StraightHallway(
                    new Position(neighbor.getLowerRight().getX() - 1,
                            r.getUpperLeft().getY(), Tileset.WALL),
                    r.getUpperLeft().getX()
                            - neighbor.getLowerRight().getX() + 2, 3,
                    i, j, true);
        } else if (r.getHallwayEdge().equals("Bottom")) {
            return new StraightHallway(
                    new Position(neighbor.getLowerRight().getX() - 1,
                            r.getLowerLeft().getY() + 3, Tileset.WALL),
                    r.getLowerLeft().getX()
                            - neighbor.getUpperRight().getX() + 2, 3,
                    i, j, true);
        } else {
            if (neighbor.getLength() > r.getLength()) {
                int mid = ((r.getLowerLeft().getY()
                        + r.getUpperLeft().getY()) / 2) + 1;
                return new StraightHallway(
                        new Position(neighbor.getLowerRight().getX()
                                - 1, mid, Tileset.WALL),
                        r.getLowerLeft().getX()
                                - neighbor.getLowerRight().getX() + 2, 3,
                        i, j, true);
            } else {
                int neighborMid = ((neighbor.getUpperRight().getY()
                        + neighbor.getLowerRight().getY()) / 2) + 1;
                return new StraightHallway(
                        new Position(neighbor.getLowerRight().getX()
                                - 1, neighborMid, Tileset.WALL),
                        r.getLowerLeft().getX()
                                - neighbor.getLowerRight().getX() + 2, 3,
                        i, j, true);
            }
        }
    }

    public StraightHallway genHallway(Room r, Room neighbor,
                                      int rIndex, int neighborIndex, int i) {
        int overlap = calcOverlap(r, neighbor, i);
        if (overlap >= 3) {
            switch (i) {
                case 0:
                    return case0(r, neighbor, rIndex, neighborIndex);
                case 1:
                    return case1(r, neighbor, rIndex, neighborIndex);
                case 2:
                    return case2(r, neighbor, rIndex, neighborIndex);
                case 3:
                    if (r.getHallwayEdge().equals("Top")) {
                        return new StraightHallway(
                                new Position(r.getUpperRight().getX()
                                        - 1, r.getUpperRight().getY(), Tileset.FLOOR),
                                neighbor.getLowerLeft().getX()
                                        - r.getUpperRight().getX() + 2, 3,
                                rIndex, neighborIndex, true);
                    } else if (r.getHallwayEdge().equals("Bottom")) {
                        return new StraightHallway(
                                new Position(r.getLowerRight().getX() - 1,
                                        r.getLowerRight().getY() + 3, Tileset.WALL),
                                neighbor.getLowerLeft().getX()
                                        - r.getUpperRight().getX() + 2, 3,
                                rIndex, neighborIndex, true);
                    } else {
                        if (neighbor.getLength() > r.getLength()) {
                            int mid = ((r.getLowerLeft().getY()
                                    + r.getUpperLeft().getY()) / 2) + 1;
                            return new StraightHallway(
                                    new Position(neighbor.getLowerRight().getX()
                                            - 1, mid, Tileset.WALL),
                                    neighbor.getLowerLeft().getX()
                                            - r.getUpperRight().getX() + 2, 3,
                                    rIndex, neighborIndex, true);
                        } else {
                            int neighborMid = ((neighbor.getUpperLeft().getY()
                                    + neighbor.getLowerLeft().getY()) / 2) + 1;
                            return new StraightHallway(
                                    new Position(r.getLowerRight().getX() - 1,
                                            neighborMid, Tileset.WALL),
                                    neighbor.getLowerLeft().getX()
                                            - r.getUpperRight().getX() + 2, 3,
                                    rIndex, neighborIndex, true);
                        }
                    }
                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    private int calcOverlap(Room r1, Room r2, int face) {
        int r1Top =  r1.getUpperLeft().getY();
        int r1Bottom = r1.getLowerLeft().getY();
        int r1Left = r1.getUpperLeft().getX();
        int r1Right = r1.getUpperRight().getX();
        int r2Top =  r2.getUpperLeft().getY();
        int r2Bottom = r2.getLowerLeft().getY();
        int r2Left = r2.getUpperLeft().getX();
        int r2Right = r2.getUpperRight().getX();
        switch (face) {
            case 0:
            case 1:
                if (r2Right >= r1Left && r2Left <= r1Left) {
                    r1.setHallwayEdge("Left");
                    return Math.min(r2Right - r1Left, r1.getWidth());
                } else if (r2Left <= r1Right && r2Right >= r1Right) {
                    r1.setHallwayEdge("Right");
                    return Math.min(r1Right - r2Left, r1.getWidth());
                } else {
                    r1.setHallwayEdge("Middle");
                    return Math.min(r1.getWidth(), r2.getWidth());
                }
            case 2:
            case 3:
                if (r2Bottom <= r1Top && r2Top >= r1Top) {
                    r1.setHallwayEdge("Top");
                    return Math.min(r1Top - r2Bottom, r1.getLength());
                } else if (r2Top >= r1Bottom && r2Bottom <= r1Bottom) {
                    r1.setHallwayEdge("Bottom");
                    return Math.min(r2Top - r1Bottom, r1.getLength());
                } else {
                    r1.setHallwayEdge("Middle");
                    return Math.min(r1.getLength(), r2.getLength());
                }
            default:
                return 0;
        }
    }



    private boolean roomsOverlap(Room r1, List<Room> rs) {
        List<Position> posR1 = r1.getPos();
        for (Room r : rs) {
            List<Position> rPos = r.getPos();
            if (!Collections.disjoint(posR1, rPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean b(int num1, int num2, int num3, int num4) {
        return (num1 >= num2 && num1 <= num3)
                || (num4 >= num2 && num4 <= num3)
                || (num4 <= num2 && num1 >= num3);
    }

    public boolean b1(int num1, int num2, int num3, int num4) {
        return (num1 <= num2 && num1 >= num4)
                || (num3 <= num2 && num3 >= num4)
                || (num3 >= num2 && num1 <= num4);
    }

    public Room nearestNeighbor(Room room, List<Room> lst, int face) {
        int absMin = 1000;
        Room nearest = null;
        int roomTop =  room.getUpperLeft().getY();
        int roomBottom =  room.getLowerLeft().getY();
        int roomLeft = room.getUpperLeft().getX();
        int roomRight = room.getUpperRight().getX();
        for (Room r : lst) {
            if (!r.equals(room)) {
                int rTop =  r.getUpperLeft().getY();
                int rBottom =  r.getLowerLeft().getY();
                int rLeft = r.getUpperLeft().getX();
                int rRight = r.getUpperRight().getX();
                int difference;
                boolean b = b(rRight, roomLeft, roomRight, rLeft);
                boolean b1 = b1(rBottom, roomTop, rTop, roomBottom);
                switch (face) {
                    case 0:
                        if (rBottom >= roomTop && b) {
                            difference = rBottom - roomTop;
                            if (difference < absMin) {
                                absMin = difference;
                                nearest = r;
                            }
                        }
                        break;
                    case 1:
                        if (rTop <= roomBottom && b) {
                            difference = roomBottom - rTop;
                            if (difference < absMin) {
                                absMin = difference;
                                nearest = r;
                            }
                        }
                        break;
                    case 2:
                        if (rRight <= roomLeft && b1) {
                            difference = roomLeft - rRight;
                            if (difference < absMin) {
                                absMin = difference;
                                nearest = r;
                            }
                        }
                        break;
                    case 3:
                        if (rLeft >= roomRight && b1) {
                            difference = rLeft - roomRight;
                            if (difference < absMin) {
                                absMin = difference;
                                nearest = r;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return nearest;
    }

    private Room genRandRoom() {
        int randWidth = RandomUtils.uniform(random, 5, (worldWidth + 1) / 4);
        int randLength = RandomUtils.uniform(random, 5, (worldLength + 1) / 4);
        int x = RandomUtils.uniform(random, 1, (worldWidth - 1));
        int y = RandomUtils.uniform(random, (worldLength - 2));
        Position upperLeft = new Position(x, y, Tileset.FLOOR);
        Room randRoom = new Room(upperLeft, randWidth, randLength);
        return randRoom;
    }

    private boolean isValidRoom(Room r, int width, int length) {
        boolean lowerLeftY =  0 <= r.getLowerLeft().getY()
                && r.getLowerLeft().getY() <= length;
        boolean lowerLeftX =  0 <= r.getLowerLeft().getX()
                && r.getLowerLeft().getX() <= width;
        boolean lowerRightY = 0 <= r.getLowerRight().getY()
                && r.getLowerRight().getY() <= length;
        boolean lowerRightX = 0 <= r.getLowerRight().getX()
                && r.getLowerRight().getX() <= width;
        boolean upperRightY = 0 <= r.getUpperRight().getY()
                && r.getUpperRight().getY() <= length;
        boolean upperRightX = 0 <= r.getUpperRight().getX()
                && r.getUpperRight().getX() <= width;
        boolean roomWidth = r.getWidth() <= width;
        boolean roomLength = r.getLength() <= length;
        return roomWidth && roomLength && lowerLeftY
                && lowerLeftX && lowerRightY && lowerRightX
                && upperRightY && upperRightX;
    }

    public List<StraightHallway> getHallways() {
        return hallways;
    }

    public List<Room> getRooms() {
        return rooms;
    }

}
