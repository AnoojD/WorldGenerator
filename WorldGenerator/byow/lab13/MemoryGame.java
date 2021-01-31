package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.rand = new Random(seed);
        //TODO: Initialize random number generator
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        String str = "";
        while(str.length() != n) {
            int randIndex = RandomUtils.uniform(this.rand, CHARACTERS.length);
            str += CHARACTERS[randIndex];
        }
        return str;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.YELLOW);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(this.width/2, this.height/2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for(int i = 0; i < letters.length(); i++) {
            Character c = letters.charAt(i);
            drawFrame(c.toString());
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String str = "";
        while(str.length() < n) {
            if(StdDraw.hasNextKeyTyped()) {
                Character keyTyped = StdDraw.nextKeyTyped();
                str += keyTyped;
                drawFrame(str);
            }
        }
        return str;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.round = 1;
        String target = "";
        String response = "";
        while(target.equals(response)) {
            StdDraw.pause(1000);
            drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            target = generateRandomString(this.round);
            flashSequence(target);
            response = solicitNCharsInput(this.round);
            this.round++;
        }
        this.round -= 1;
        this.gameOver = true;
        drawFrame("Game Over! You made it to round: " + this.round);
        //TODO: Establish Engine loop
    }

}
