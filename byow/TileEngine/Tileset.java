package byow.TileEngine;

import java.awt.Color;
import java.io.Serializable;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 * <p>
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 * <p>
 * Ex:
 * world[x][y] = Tileset.FLOOR;
 * <p>
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset implements Serializable {
    public static final TETile VATAR = new TETile('@', Color.white, Color.black, "you");

    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");

    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile NEW_WALL = new TETile('#', Color.gray, Color.black,
            "newwall", "/Users/mcicco/Desktop/test.png");
    public static final TETile NEW_FLOOR = new TETile('·', Color.green, Color.black,
            "newfloor", "/Users/mcicco/Desktop/NEW_FLOOR.png");
    public static final TETile POND = new TETile('$', Color.blue, Color.black,
            "pond", "/Users/mcicco/Desktop/POND.png");
    public static final TETile HUG = new TETile('*', Color.red, Color.black,
            "hug", "/Users/mcicco/Desktop/HUG.png");
    public static final TETile SAHAI = new TETile('8', Color.yellow, Color.black,
            "sahai", "/Users/mcicco/Desktop/SAHAI.png");
    public static final TETile ENEMY = new TETile('<', Color.white, Color.black, ""
            + "ghost", "/Users/mcicco/Desktop/GHOST.png");
}


