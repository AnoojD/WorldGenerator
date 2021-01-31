package byow.Phase2;

import byow.Core.Engine;
import byow.Phase1.Position;
import byow.Phase1.SampleWorld;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Font;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class LoadScreen {
    private final int width = 50;
    private final int height = 50;
    private String fileName = "";
    private String seedStr = "";
    private boolean gameStart = false;
    private String replay = "";
    private TETile[][] grid;
    private TERenderer renderer = new TERenderer();
    private boolean gameEnd = false;
    private SampleWorld sw;
    private boolean saveReady = false;
    private Clip clip;

    public void displayMenu() {
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.text(this.width / 2, this.height / 2 + 10, "CS 61B Proj 3");
        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.text(this.width / 2, this.height / 2, "New Game (N)");
        StdDraw.text(this.width / 2, this.height / 2 - 2, "Load Game (L)");
        StdDraw.text(this.width / 2, this.height / 2 - 4, "Quit Game (Q)");
    }

    public static void main(String[] args) {
        LoadScreen ls = new LoadScreen();
        ls.pathWays();
    }




    public void drawFrame(String s) {
        StdDraw.clear(Color.WHITE);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(this.width / 2, this.height / 2, s);
        StdDraw.show();
    }


    public void generateWorld(String input) {
        sw.createWorld(Long.parseLong(input));
        grid = sw.getWorld();
        renderer.initialize(65, 50);
        renderer.renderFrame(sw.getWorld());
    }


    public void pathWays() {
        displayMenu();
        while (!gameStart) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = getNextKey();
            fileName += c;
            if (c == 'N') {
                sw = new SampleWorld(65, 50);
                drawFrame("Enter numerical seed (Press S after to finish): ");
            }
            if (Character.isDigit(c)) {
                seedStr += c;
                drawFrame(fileName.substring(1));
            } else if (c == 'S') {
                gameStart = true;
                generateWorld(seedStr);
                sw.addPlayer(sw.getWorld());
            } else if (c == 'L') {
                gameStart = true;
                String in = readFile(System.getProperty("user.dir")
                        + File.separator + "prevWorld.txt");
                fileName = in.substring(0, in.length() - 2);
                Engine eng = new Engine();
                TETile[][] loaded = eng.interactWithInputString(in.substring(0, in.length() - 2));
                sw = eng.getSw();
                sw.setWorld(loaded);
                grid = loaded;
                renderer.initialize(65, 50);
                renderer.renderFrame(grid);
            } else if (c == 'Q') {
                System.exit(0);
            }
        }
    }

    public Position findPlayer(TETile[][] w) {
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[0].length; j++) {
                if (w[i][j].equals(Tileset.AVATAR)) {
                    return new Position(i, j, Tileset.AVATAR);
                }
            }
        }
        return null;
    }

    public void keyBoardInput() {
        while (!gameStart) {
            pathWays();
        }
        while (!gameEnd) {
            renderer.renderFrame(grid);
            huddy();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = getNextKey();
            fileName += c;
            if (c == 'W' || c == 'S' || c == 'A' || c == 'D') {
                updateWorld(c);
            } else if (c == ':') {
                saveReady = true;
            } else if (saveReady && c == 'Q') {
                gameEnd = true;
                try {
                    File savedGame = new File(System.getProperty("user.dir")
                            + File.separator + "prevWorld.txt");
                    savedGame.createNewFile();
                    writeFile(savedGame.getName());
                } catch (IOException e) {
                    System.out.println("Error occurred");
                }
                System.exit(0);
            } else {
                fileName = fileName.substring(0, fileName.length() - 1);
            }
        }

    }

    public void updateWorld(char ch) {
        sw.movePlayer(ch);
        grid = sw.getWorld();
        renderer.renderFrame(grid);
    }

    public void huddy() {
        String str = tileType();
        Font font = new Font("Monaco", Font.BOLD, 10);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(5, 49, "Tile type: " + str);
        StdDraw.show();
    }

    private String tileType() {
        int mX = (int) StdDraw.mouseX();
        int mY = (int) StdDraw.mouseY();
        if (validMouse(mX, mY)) {
            TETile curr = grid[mX][mY];
            if (curr.equals(Tileset.FLOOR)) {
                return "Floor";
            } else if (curr.equals(Tileset.WALL)) {
                return "Wall";
            } else if (curr.equals(Tileset.AVATAR)) {
                return "Avatar";
            } else if (curr.equals(Tileset.LAVATAR)) {
                return "Light Avatar";
            } else if (curr.equals(Tileset.LIGHTFLOOR)) {
                return "Light Floor";
            } else if (curr.equals(Tileset.AVATARWLIGHT)) {
                return "Avatar w/ Light";
            } else if (curr.equals(Tileset.AVATARWOLIGHT)) {
                return "Avatar w/o Light";
            } else if (curr.equals(Tileset.TREE)) {
                return "Torch";
            } else {
                return "Nether";
            }
        }
        return "Nether";
    }

    private boolean validMouse(int x, int y) {
        return (x >= 0) && (x < 65) && (y >= 0) && (y < 50);

    }

    public void writeFile(String name) {
        try {
            BufferedWriter fr = new BufferedWriter(new FileWriter(name));
            fr.write(fileName);
            fr.close();
        } catch (IOException e) {
            System.out.println("File does not exist!");
        }
    }

    public String readFile(String name) {
        String res = "";
        try {
            BufferedReader fr = new BufferedReader(new FileReader(name));
            String strCurrLine = fr.readLine();
            while (strCurrLine != null) {
                res += strCurrLine;
                strCurrLine = fr.readLine();
            }
        } catch (IOException e) {
            System.out.println("File does not exist!");
        }
        return res;
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

}
