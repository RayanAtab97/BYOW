package byow.OurWorld;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

/**
 * @author mcicco adapted from Josh Hug's Editor class
 */


public class Interactivity implements Serializable {
    private int width = 70;
    private int height = 50;
    private RectWorld w1;
    private boolean enemyMove = false;
    private TETile avatarTETile = Tileset.SAHAI;


    public void showStats(RectWorld w) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(5, height - 5, "Stats:");

        StdDraw.text(5, height - 7, "Current Level: " + w.getLevel());
        StdDraw.text(5, height - 9, "User Score: " + w.getAvatarScore());
        StdDraw.text(5, height - 11, "Enemy Score: " + w.getEnemyScore());
        StdDraw.show();
    }

    public void initialize() {
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void show() {
        int midWidth = width / 2;
        int midHeight = height / 2;
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(midWidth, midHeight + 10, "CS 61B: The Game");
        StdDraw.text(midWidth, midHeight - 5, "New Game (N)");
        StdDraw.text(midWidth, midHeight - 10, "Load Game (L)");
        StdDraw.text(midWidth, midHeight - 15, "Quit (Q)");
        StdDraw.show();
    }

    public void newGame() {
        String seedString = "";
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2, (height / 2) + 10,
                "Please enter a seed followed by 'S' to begin: ");
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                char cC = Character.toLowerCase(c);
                if (cC == 's') {
                    override(seedString);
                    renderGame(seedString);
                    break;
                }
                seedString += c;
            }
            StdDraw.show();
        }
    }

    public void renderGame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2, (height / 2) + 10, "Please choose a character:");
        StdDraw.text(width / 2, (height / 2) + 5, "SAHAI (S)");
        StdDraw.text(width / 2, (height / 2), "HUG (H)");
        StdDraw.text(width / 2, (height / 2) - 5, "FIGHTER STATISTICS (F)");
        //standard
        avatarTETile = null;
        while (avatarTETile == null) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                char cC = Character.toLowerCase(c);
                switch (cC) {
                    case 's':
                        avatarTETile = Tileset.SAHAI;
                        break;
                    case 'h':
                        avatarTETile = Tileset.HUG;
                        break;
                    case 'f':
                        StdDraw.clear(Color.BLACK);
                        StdDraw.setPenColor(Color.WHITE);
                        StdDraw.text(width / 2, (height / 2) + 15, "Stats:");

                        StdDraw.text(width / 4, (height / 2) + 10, "Speed:");

                        StdDraw.text(2 * width / 4, (height / 2) + 10, "Hug: 82/100 ");
                        StdDraw.text(3 * width / 4, (height / 2) + 10, "Sahai: 62/100");
                        StdDraw.text(width / 4, (height / 2) + 5, "Power:");
                        StdDraw.text(2 * width / 4, (height / 2) + 5, "Hug: 55/100");
                        StdDraw.text(3 * width / 4, (height / 2) + 5, "Sahai: 73/100");
                        StdDraw.text(width / 4, (height / 2),
                                "Famous Quotes:");
                        StdDraw.text(2 * width / 4, (height / 2) - 5,
                                "Hug: Stackoverflow will become your best friend");
                        StdDraw.text(2 * width / 4, (height / 2) - 10,
                                "Sahai: You need to appreciate the level of your stuckness");
                        StdDraw.text(width / 2, (height / 2) - 15,
                                "Choose your fighter! Hug(H), Sahai(S)");
                        break;
                    default:
                        break;
                }
            }
            StdDraw.show();
        }
        long seed = Long.parseLong(s);
        startGame(seed, 1, 0, 0);
    }

    public void endGame(boolean playerWin) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        System.out.println("---------");
        StdDraw.text(width / 2, (height / 2) + 10, "GAME OVER!");
        if (playerWin) {
            StdDraw.text(width / 2, (height / 2) + 5, "YOU WON!");
        } else {
            StdDraw.text(width / 2, (height / 2) + 5, "YOU LOST!");
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void gameInteraction(RectWorld wr) {
        TERenderer ter = new TERenderer();
        ter.initialize(70, 50);
        String s = "";

        while (true) {
            ter.renderFrame(wr.getWorld());
            ter.mouseChecking(wr);
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                char cC = Character.toLowerCase(c);
                s += cC;
                for (int i = 0; i < s.length() - 1; i++) {
                    if (s.charAt(i) == ':' && s.charAt(i + 1) == 'q') {
                        saveEditor(wr);
                    }
                }
                switch (cC) {
                    case 'q':
                        System.exit(0);
                        break;
                    case 'w':
                        wr.moveUp();
                        enemyMove = wr.moveEnemy();
                        break;
                    case 'd':
                        wr.moveRight();
                        enemyMove = wr.moveEnemy();
                        break;
                    case 's':
                        wr.moveDown();
                        enemyMove = wr.moveEnemy();
                        break;
                    case 'a':
                        wr.moveLeft();
                        enemyMove = wr.moveEnemy();
                        break;
                    default:
                        break;
                }
                wr.checkKey();


            }
            if (w1.getEnemyScore() == 3) {
                System.out.println("enemy won");
                while (true) {
                    endGame(false);
                }
            } else if (w1.getAvatarScore() == 3) {
                while (true) {
                    endGame(true);
                }
            } else if (enemyMove || w1.isOpenDoor()) { //enemy won or player won
                w1.incrementLevel();
                if (!enemyMove) {
                    w1.incrementAvatarScore();
                } else { //enemy won
                    w1.incrementEnemyScore();
                }
                enemyMove = false;

                startGame(new Random().nextLong(), w1.getLevel(), w1.getAvatarScore(),
                        w1.getEnemyScore());
            }
            showStats(w1);
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
    }


    public void loadGame(RectWorld w) {
        boolean doorOpen = w.isOpen();
        w1 = new RectWorld(w.getWorld(), w.getWIDTH(), w.getHEIGHT(), w.getWorldSeed(),
                w.getLevel(), w.getAvatarScore(), w.getEnemyScore());
        w1.rectangleWorld();
        w1.addDoor(w.getDoorPosition());
        w1.addKey(w.getKeyPosition());
        w1.addAvatar(w.getAvatarPos(), w.getAvatar().getAvatarTile());
        w1.addEnemy(w.getEnemy().getAvatarPos());
        if (doorOpen) {
            w1.lockedToUnlocked(w.getDoorPosition());
            System.out.println("hello");
        }
        StdDraw.show();
        gameInteraction(w1);
    }

    public void startGame(long seed, int level, int aScore, int eScore) {
        TERenderer ter = new TERenderer();
        ter.initialize(70, 50);
        TETile[][] tiles = null;
        w1 = new RectWorld(tiles, 70, 50, seed, level, aScore, eScore);
        w1.rectangleWorld();
        w1.addDoor();
        w1.addKey();
        w1.addAvatar(avatarTETile);
        w1.addEnemy();
        gameInteraction(w1);
    }

    public void override(String s) {
        File f = new File("./save_data");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter("./save_data"));
            out.write(s);
            out.close();

        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }

    }

    public static void saveEditor(RectWorld w1) {
        File f = new File("./world_data");
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
        File f = new File("./world_data");
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
}
