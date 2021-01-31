package byow.Core;

import byow.InputDemo.StringInputDevice;
import byow.Phase1.SampleWorld;
import byow.Phase2.LoadScreen;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.io.*;


public class Engine {
    private TERenderer ter = new TERenderer();
    private SampleWorld sw;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 65;
    public static final int HEIGHT = 50;


    /**
     * Method used for exploring a fresh world. This method should handle all
     * inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        LoadScreen ls = new LoadScreen();
        ls.keyBoardInput();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {

        StringInputDevice sid = new StringInputDevice(input);
        String seedStr = "";
        while (sid.possibleNextInput()) {
            char c = sid.getNextKey();
            if (c == 'N' || c == 'n') {
                sw = new SampleWorld(WIDTH, HEIGHT);
            } else if (Character.isDigit(c)) {
                seedStr += c;
            } else if (c == 'L' || c == 'l') {
                String in = readFile(System.getProperty("user.dir")
                        + File.separator + "savedWorld.txt");
                return interactWithInputString(in.substring(0, in.length() - 2)
                        + input.substring(1));
            } else if (c == 'S' || c == 's') {
                sw.createWorld(Long.parseLong(seedStr));
                break;
            }
        }
        sw.addPlayer(sw.getWorld());
        boolean toSave = false;
        while (sid.possibleNextInput()) {
            char c = sid.getNextKey();
            if (c == 'W' || c == 'w') {
                sw.movePlayer(c);
            } else if (c == 'S' || c == 's') {
                sw.movePlayer(c);
            } else if (c == 'A' || c == 'a') {
                sw.movePlayer(c);
            } else if (c == 'D' || c == 'd') {
                sw.movePlayer(c);
            } else if (c == ':') {
                toSave = true;
            } else if (toSave && (c == 'Q' || c == 'q')) {
                try {
                    File savedWorld = new File(System.getProperty("user.dir")
                            + File.separator + "savedWorld.txt");
                    savedWorld.createNewFile();
                    writeFile(savedWorld.getName(), input);
                } catch (IOException e) {
                    System.out.println("Error occurred");
                }
            }
        }
//                while (sid.possibleNextInput()) {
//                    c = sid.getNextKey();
//                    if (c != 'S' && c != 's') {
//                        lst.add(c);
//                    }
//                }
//                String seedStr = "";
//                for (int i = 0; i < lst.size(); i++) {
//                    seedStr += lst.get(i);
//                }
//                sw.createWorld(Long.parseLong(seedStr));
//                return sw.getWorld();
//            }
        return sw.getWorld();
    }

    public SampleWorld getSw() {
        return sw;
    }

    public void writeFile(String name, String fileName) {
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

    public void setSw(SampleWorld sample) {
        sw = sample;
    }

    public void generateWorld(TETile[][] world) {
        TERenderer t = new TERenderer();
        t.initialize(WIDTH, HEIGHT);
        t.renderFrame(world);
    }

}
