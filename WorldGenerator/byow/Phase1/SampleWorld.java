package byow.Phase1;

import byow.Core.Engine;
import byow.Phase2.Player;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdAudio;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleWorld {
    private int width;
    private int height;
    private TETile[][] world;
    private String fileName;
    private Player player;
    private Map<Position, Room> torches;
    private double[] b = StdAudio.read(System.getProperty("user.dir")
            + File.separator + "move.wav");
    private double[] sound2 = StdAudio.read(System.getProperty("user.dir")
            + File.separator + "switch-1.wav");

    public SampleWorld(int w, int h) {
        width = w;
        height = h;
        world = new TETile[w][h];
        player = new Player(new Position(0, 0, Tileset.AVATAR));
    }

    public void createWorld(Long seed) {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        Tesselation tess = new Tesselation(seed, width, height);
        List<Room> rooms = tess.getRooms();
        List<StraightHallway> halls = tess.getHallways();

        for (Room r : rooms) {
            addRoom(r, world);
        }
        torches = addLights(world, rooms);
        for (StraightHallway h : halls) {
            addHallway(h, world);
        }

    }

    public Player getPlayer() {
        return this.player;
    }

    public Map<Position, Room> addLights(TETile[][] w, List<Room> r) {
        Map<Position, Room> res = new HashMap<>();
        for (Room rum : r) {
            boolean one = true;
            List<Position> lst = rum.getPos();
            for (Position p : lst) {
                for (int i = 0; i < w.length; i++) {
                    for (int j = 0; j < w[0].length; j++) {
                        Position temp = new Position(i, j, w[i][j]);
                        if (one && p.equals(temp) && w[i][j].equals(Tileset.FLOOR)) {
                            one = false;
                            w[i][j] = Tileset.TREE;
                            res.put(new Position(i, j, Tileset.TREE), rum);
                        }
                    }
                }
            }
        }
        return res;
    }

    public void lightUp(Room r, TETile[][] w) {
        List<Position> lst = r.getPos();
        for (Position p : lst) {
            if (p.getTile().equals(Tileset.FLOOR)) {
                TETile nFloor = Tileset.LIGHTFLOOR;
                w[p.getX()][p.getY()] = nFloor;
            }
        }
    }
    public int countLighted(Room r, TETile[][] w) {
        List<Position> lst = r.getPos();
        int count = 0;
        for (Position p : lst) {
            if (w[p.getX()][p.getY()].equals(Tileset.LIGHTFLOOR)) {
                count++;
            }
        }
        return count;
    }

    public void lightDown(Room r, TETile[][] w) {
        List<Position> lst = r.getPos();
        for (Position p : lst) {
            if (w[p.getX()][p.getY()].equals(Tileset.LIGHTFLOOR)) {
                TETile nFloor = Tileset.FLOOR;
                w[p.getX()][p.getY()] = nFloor;
            }
        }
    }

    public void movePlayer(char c) {
        if (c == 'W' || c == 'w') {
            if (validMove(moveUp(getPlayer().getUserPos()))) {
                int oldX = getPlayer().getUserPos().getX();
                int oldY = getPlayer().getUserPos().getY();
                Position temp = moveUp(getPlayer().getUserPos());
                if (world[temp.getX()][temp.getY()].equals(Tileset.FLOOR)
                    && world[oldX][oldY].equals(Tileset.LAVATAR)) {
                    world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    this.getPlayer().changePos(moveUp(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.AVATAR;
                } else if (world[temp.getX()][temp.getY()].equals(Tileset.LIGHTFLOOR)) {
                    if (world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else if (world[oldX][oldY].equals(Tileset.AVATAR)) {
                        world[oldX][oldY] = Tileset.FLOOR;
                    } else {
                        world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    }
                    this.getPlayer().changePos(moveUp(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.LAVATAR;
                } else {
                    if (world[oldX][oldY].equals(Tileset.AVATARWOLIGHT)
                        || world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else {
                        world[oldX][oldY] = Tileset.FLOOR;
                    }
                    this.getPlayer().changePos(moveUp(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    if (onLight(getPlayer().getUserPos())) {
                        StdAudio.play(sound2);
                        int lCount = countLighted(torches.get(getPlayer().getUserPos()), world);
                        if (lCount == 0) {
                            lightUp(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWLIGHT;
                        } else {
                            lightDown(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWOLIGHT;
                        }
                    } else {
                        world[pX][pY] = getPlayer().getUserPos().getTile();
                    }
                }
            } else {
                StdAudio.play(b);
            }
        } else if (c == 'A' || c == 'a') {
            if (validMove(moveLeft(getPlayer().getUserPos()))) {
                int oldX = getPlayer().getUserPos().getX();
                int oldY = getPlayer().getUserPos().getY();
                Position temp = moveLeft(getPlayer().getUserPos());
                if (world[temp.getX()][temp.getY()].equals(Tileset.FLOOR)
                        && world[oldX][oldY].equals(Tileset.LAVATAR)) {
                    world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    this.getPlayer().changePos(moveLeft(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.AVATAR;
                } else if (world[temp.getX()][temp.getY()].equals(Tileset.LIGHTFLOOR)) {
                    if (world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else if (world[oldX][oldY].equals(Tileset.AVATAR)) {
                        world[oldX][oldY] = Tileset.FLOOR;
                    } else {
                        world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    }
                    this.getPlayer().changePos(moveLeft(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.LAVATAR;
                } else {
                    if (world[oldX][oldY].equals(Tileset.AVATARWOLIGHT)
                        || world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else {
                        world[oldX][oldY] = Tileset.FLOOR;
                    }
                    this.getPlayer().changePos(moveLeft(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    if (onLight(getPlayer().getUserPos())) {
                        StdAudio.play(sound2);
                        int lCount = countLighted(torches.get(getPlayer().getUserPos()), world);
                        if (lCount == 0) {
                            lightUp(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWLIGHT;
                        } else {
                            lightDown(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWOLIGHT;
                        }
                    } else {
                        world[pX][pY] = getPlayer().getUserPos().getTile();
                    }
                }
            } else {
                StdAudio.play(b);
            }
        } else if (c == 'D' || c == 'd') {
            if (validMove(moveRight(getPlayer().getUserPos()))) {
                int oldX = getPlayer().getUserPos().getX();
                int oldY = getPlayer().getUserPos().getY();
                Position temp = moveRight(getPlayer().getUserPos());
                if (world[temp.getX()][temp.getY()].equals(Tileset.FLOOR)
                        && world[oldX][oldY].equals(Tileset.LAVATAR)) {
                    world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    this.getPlayer().changePos(moveRight(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.AVATAR;
                } else if (world[temp.getX()][temp.getY()].equals(Tileset.LIGHTFLOOR)) {
                    if (world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else if (world[oldX][oldY].equals(Tileset.AVATAR)) {
                        world[oldX][oldY] = Tileset.FLOOR;
                    } else {
                        world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    }
                    this.getPlayer().changePos(moveRight(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.LAVATAR;
                } else {
                    if (world[oldX][oldY].equals(Tileset.AVATARWOLIGHT)
                        || world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else {
                        world[oldX][oldY] = Tileset.FLOOR;
                    }
                    this.getPlayer().changePos(moveRight(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    if (onLight(getPlayer().getUserPos())) {
                        StdAudio.play(sound2);
                        int lCount = countLighted(torches.get(getPlayer().getUserPos()), world);
                        if (lCount == 0) {
                            lightUp(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWLIGHT;
                        } else {
                            lightDown(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWOLIGHT;
                        }
                    } else {
                        world[pX][pY] = getPlayer().getUserPos().getTile();
                    }
                }
            } else {
                StdAudio.play(b);
            }
        } else if (c == 'S' || c == 's') {
            if (validMove(moveDown(getPlayer().getUserPos()))) {
                int oldX = getPlayer().getUserPos().getX();
                int oldY = getPlayer().getUserPos().getY();
                Position temp = moveDown(getPlayer().getUserPos());
                if (world[temp.getX()][temp.getY()].equals(Tileset.FLOOR)
                        && world[oldX][oldY].equals(Tileset.LAVATAR)) {
                    world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    this.getPlayer().changePos(moveDown(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.AVATAR;
                } else if (world[temp.getX()][temp.getY()].equals(Tileset.LIGHTFLOOR)) {
                    if (world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else if (world[oldX][oldY].equals(Tileset.AVATAR)) {
                        world[oldX][oldY] = Tileset.FLOOR;
                    } else {
                        world[oldX][oldY] = Tileset.LIGHTFLOOR;
                    }
                    this.getPlayer().changePos(moveDown(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    world[pX][pY] = Tileset.LAVATAR;
                } else {
                    if (world[oldX][oldY].equals(Tileset.AVATARWOLIGHT)
                        || world[oldX][oldY].equals(Tileset.AVATARWLIGHT)) {
                        world[oldX][oldY] = Tileset.TREE;
                    } else {
                        world[oldX][oldY] = Tileset.FLOOR;
                    }
                    this.getPlayer().changePos(moveDown(getPlayer().getUserPos()));
                    int pX = getPlayer().getUserPos().getX();
                    int pY = getPlayer().getUserPos().getY();
                    if (onLight(getPlayer().getUserPos())) {
                        StdAudio.play(sound2);
                        int lCount = countLighted(torches.get(getPlayer().getUserPos()), world);
                        if (lCount == 0) {
                            lightUp(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWLIGHT;
                        } else {
                            lightDown(torches.get(getPlayer().getUserPos()), world);
                            world[pX][pY] = Tileset.AVATARWOLIGHT;
                        }
                        world[pX][pY] = Tileset.AVATARWLIGHT;
                    } else {
                        world[pX][pY] = getPlayer().getUserPos().getTile();
                    }
                }
            } else {
                StdAudio.play(b);
            }
        }
    }

    public boolean validMove(Position p) {
        return !world[p.getX()][p.getY()].equals(Tileset.WALL);

    }

    public boolean onLight(Position p) {
        return world[p.getX()][p.getY()].equals(Tileset.TREE);
    }

    public Position moveUp(Position p) {
        return new Position(p.getX(), p.getY() + 1, Tileset.AVATAR);
    }

    public Position moveDown(Position p) {
        return new Position(p.getX(), p.getY() - 1, Tileset.AVATAR);
    }

    public Position moveLeft(Position p) {
        return new Position(p.getX() - 1, p.getY(), Tileset.AVATAR);
    }

    public Position moveRight(Position p) {
        return new Position(p.getX() + 1, p.getY(), Tileset.AVATAR);
    }


    public void addPlayer(TETile[][] w) {
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[0].length; j++) {
                if (w[i][j].equals(Tileset.FLOOR)) {
                    Position newP = new Position(i, j, Tileset.AVATAR);
                    player.changePos(newP);
                    w[i][j] = player.getUserPos().getTile();
                    return;
                }
            }
        }
    }



    public static void main(String[] args) {
        String input = "N34532S";
        Engine eng = new Engine();
        TETile[][] world = eng.interactWithInputString(input);
        eng.generateWorld(world);
    }

    public static void addRoom(Room r, TETile[][] world) {
        List<Position> pos = r.getPos();
        for (Position p : pos) {
            world[p.getX()][p.getY()] = p.getTile();
        }
    }


    public static void addHallway(StraightHallway h, TETile[][] world) {
        List<Position> pos = h.getPos();
        for (Position p : pos) {
            if (world[p.getX()][p.getY()].equals(Tileset.FLOOR)) {
                world[p.getX()][p.getY()] = Tileset.FLOOR;
            } else {
                world[p.getX()][p.getY()] = p.getTile();
            }
        }
    }



    public void setWorld(TETile[][] w) {
        world = w;
    }

    public TETile[][] getWorld() {
        return world;
    }
}

