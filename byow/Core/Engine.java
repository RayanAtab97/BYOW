package byow.Core;

import byow.OurWorld.Interactivity;
import byow.OurWorld.RectWorld;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static junit.framework.TestCase.assertTrue;


public class Engine implements Serializable {
    /* Feel free to change the width and height. */

    public static final int WIDTH = 70;
    public static final int HEIGHT = 50;
    private RectWorld w = null;
    private long worldSeed;


    public static void main(String[] args) {

        Engine test = new Engine();
        test.interactWithKeyboard();
        //test.interactWithInputString("n9127564470038628925sdaddawwawaswasaasswadadaadds");
        //test.interactWithInputString("n9127564470038628925sdaddawwawas:q");
        //test.interactWithInputString("lwasaasswadada:q");
       //  test.interactWithInputString("ladds");
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Interactivity e = new Interactivity();
        e.initialize();
        e.show();
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                switch (c) {
                    case 'L':
                        e.loadGame(e.loadEditor());
                        break;
                    case 'Q':
                        System.exit(0);
                        break;
                    case 'N':
                        e.newGame();
                        break;
                    default:
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        w = new RectWorld(null, 70, 50, 0, 1, 0, 0);
        Long seed = 0L;
        String seedString = "";
        String userInput = input.toLowerCase();
        for (int i = 1; i < userInput.length() - 1; i++) {
            if (Character.isDigit(userInput.charAt(i))) {
                seedString += userInput.charAt(i);
            }

        }

        if (seedString.length() != 0) {
            seed = Long.parseLong(seedString);
        }

        if (userInput.length() != 0) {
            if (userInput.charAt(0) == 'n') {
                w = new RectWorld(null, 70, 50, seed, 1, 0, 0);
                w.rectangleWorld();
                w.addDoor();
                w.addKey();
                w.addAvatar(Tileset.SAHAI);
                w.addEnemy();
            }
            if (userInput.charAt(0) == 'l') {
                w = loadEditor();
                loadGame(w);
            }
        }

        for (int i = 0; i < userInput.length(); i++) {
            char c = userInput.charAt(i);
            if (c == ':' && userInput.charAt(i + 1) == 'q') {
                saveEditor(w);
            }
            switch (c) {
                case 'w':
                    w.moveUp();
                    w.moveEnemy();
                    break;
                case 'd':
                    w.moveRight();
                    w.moveEnemy();
                    break;
                case 's':
                    w.moveDown();
                    w.moveEnemy();
                    break;
                case 'a':
                    w.moveLeft();
                    w.moveEnemy();
                    break;
                default:
                    break;
            }
        }
        return w.getFrame();
    }

    public static void saveEditor(RectWorld w1) {
        File f = new File("./world_data.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w1);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public RectWorld loadEditor() {
        File f = new File("./world_data.txt");
        // System.out.println("hello");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (RectWorld) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        /* In the case no Editor has been saved yet, we return a new one. */
        return new RectWorld(null, 70, 50, 31321, 1, 0, 0);
    }


    public void loadGame(RectWorld w1) {
        boolean doorOpen = w.isOpen();
        w = new RectWorld(w.getWorld(), w1.getWIDTH(), w1.getHEIGHT(), w1.getWorldSeed(),
                w1.getLevel(), w1.getAvatarScore(), w1.getEnemyScore());
        w.rectangleWorld();
        w.addDoor(w1.getDoorPosition());
        w.addKey(w1.getKeyPosition());
        w.addAvatar(w1.getAvatarPos(), w1.getAvatar().getAvatarTile());
        w.addEnemy(w1.getEnemy().getAvatarPos());
        if (doorOpen) {
            w.lockedToUnlocked(w1.getDoorPosition());
        }

    }

    @Test
    public void testWorlds() {
        TETile[][] s1 =
                interactWithInputString("n9127564470038628925sdaddawwawaswasaasswadadaadds");
        TETile[][] s2 = interactWithInputString("n9127564470038628925sdaddawwawas:q");
        TETile[][] s3 = interactWithInputString("lwasaasswadada:q");
        TETile[][] s4 = interactWithInputString("ladds");
        for (int i = 0; i < 70; i++) {
            for (int j = 0; j < 50; j++) {
                System.out.println(i + " " + j);
                //  System.out.println("S1:" + s1[i][j]);
                //System.out.println("S4:" + s4[i][j]);
                System.out.println(s1[i][j].description());
                System.out.println(s4[i][j].description());
                assertTrue(s1[i][j].description().equals(s4[i][j].description()));
            }
        }
    }
}
