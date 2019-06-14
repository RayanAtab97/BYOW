package byow.OurWorld;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;

public class RectWorld implements Serializable {
    private TETile[][] world;
    private int WIDTH;
    private int HEIGHT;
    private static Random rand;
    private List<Rectangle> rectangles;
    private Random tileRand;
    private Avatar avatar;
    private Avatar enemy;
    private Position doorPosition;
    private Position keyPosition;
    private int level;
    private int avatarScore;
    private int enemyScore;
    private long worldSeed;
    boolean unlockedDoor = false;


    public RectWorld(TETile[][] w, int wi, int h, long seed,
                     int level, int avatarScore, int enemyScore) {
        this.level = level;
        this.enemyScore = enemyScore;
        this.avatarScore = avatarScore;
        worldSeed = seed;
        world = w;
        WIDTH = wi;
        HEIGHT = h;
        rand = new Random();
        rand.setSeed(seed);
        rectangles = new ArrayList<>();
        world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        tileRand = new Random(seed);
    }

    public Position getAvatarPos() {
        return avatar.getAvatarPos();
    }

    public class PlayerComp implements Comparator<Rectangle> {
        public int compare(Rectangle r1, Rectangle r2) {
            Position a = r1.getUpperLeft();
            Position b = r2.getUpperLeft();
            if (a.equals(b)) {
                return 0;
            }
            Position aP = getAvatarPos();
            double aX = aP.getX() - a.getX();
            double bX = aP.getX() - b.getX();
            double aY = aP.getY() - a.getY();
            double bY = aP.getY() - b.getY();
            double distA = Math.sqrt(Math.pow(aX, 2) + Math.pow(aY, 2));
            double distB = Math.sqrt(Math.pow(bX, 2) + Math.pow(bY, 2));
            if (distA < distB) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public int getAvatarScore() {
        return avatarScore;
    }

    public int getEnemyScore() {
        return enemyScore;
    }

    public int getLevel() {
        return level;
    }

    public void incrementEnemyScore() {
        enemyScore += 1;
    }

    public void incrementAvatarScore() {
        avatarScore += 1;
    }

    public void incrementLevel() {
        level += 1;
    }

    public Position getDoorPosition() {
        return doorPosition;
    }

    public Position getKeyPosition() {
        return keyPosition;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public Avatar getEnemy() {
        return enemy;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void addKey() {
        Rectangle randRect = rectangles.get(genRandom(rectangles.size()));
        int xPos = randRect.getUpperLeft().getX();
        int yPos = randRect.getUpperLeft().getY();
        world[xPos + 1][yPos - 1] = Tileset.POND;
        keyPosition = new Position(xPos + 1, yPos - 1);
    }

    public void addKey(Position p) {
        world[p.getX()][p.getY()] = Tileset.POND;
        keyPosition = new Position(p.getX(), p.getY());
    }

    public void addDoor(Position p) {
        world[p.getX()][p.getY()] = Tileset.LOCKED_DOOR;
        doorPosition = new Position(p.getX(), p.getY());
    }

    public void addDoor() {

        Rectangle randRect = rectangles.get(genRandom(rectangles.size()));
        int xPos = randRect.getUpperLeft().getX();
        int yPos = randRect.getUpperLeft().getY();

        if (world[xPos][yPos - 1]
                == Tileset.NEW_WALL && world[xPos + 1][yPos] == Tileset.NEW_WALL) {
            world[xPos + 1][yPos] = Tileset.LOCKED_DOOR;
            doorPosition = new Position(xPos + 1, yPos);
        } else {
            world[xPos][yPos] = Tileset.LOCKED_DOOR;
            doorPosition = new Position(xPos, yPos);
        }


    }

    public void lockedToUnlocked(Position p) {
        world[p.getX()][p.getY()] = Tileset.UNLOCKED_DOOR;
    }

    public boolean isOpen() {
        return unlockedDoor;

    }


    /**
     * returns a heuristic given an input distance t the door
     * used by the method move enemy to implement movements of the npc
     * the heuristic is determined using the euclidean distance
     */
    private double h(Position p) {
        int startX = p.getX();
        int startY = p.getY();
        return Math.sqrt(Math.pow(doorPosition.getX() - startX, 2)
                + Math.pow(doorPosition.getY() - startY, 2));

    }

    private List<Position> shortestPath(Position start, Position end) {
        HashSet<Position> visited = new HashSet<>();
        HashMap<Position, Position> cameFrom = new HashMap<>();
        HashMap<Position, Double> costSoFar = new HashMap<>();
        DoubleMapPQ<Position> fringe = new DoubleMapPQ<>();
        fringe.add(start, 0.00);
        cameFrom.put(start, null);
        costSoFar.put(start, 0.0);
        Position current;
        Position pNew = null;
        // double time = System.currentTimeMillis();
        while (fringe.size() > 0) {
            current = fringe.removeSmallest();

            visited.add(current);
            if (end.equals(current)) {
                pNew = current;

                break;
            }
            List<Position> possiblePositions = new ArrayList<>();
            int xPos = current.getX();
            int yPos = current.getY();
            double priority;
            possiblePositions.add(new Position(xPos + 1, yPos));
            possiblePositions.add(new Position(xPos - 1, yPos));
            possiblePositions.add(new Position(xPos, yPos + 1));
            possiblePositions.add(new Position(xPos, yPos - 1));


            for (Position p : possiblePositions) {
                if (p.getX() >= WIDTH || p.getX() < 0 || p.getY() >= HEIGHT || p.getY() < 0) {
                    continue;
                }
                if (world[p.getX()][p.getY()].equals(Tileset.NOTHING)
                        || world[p.getX()][p.getY()].equals(Tileset.NEW_WALL)) {
                    continue;
                } else if (!p.equals(current) && !visited.contains(p)) {
                    double newCost = costSoFar.get(current) + 1;
                    if (!costSoFar.containsKey(p) || newCost < costSoFar.get(p)) {
                        costSoFar.put(p, newCost);
                        priority = newCost + h(p);
                        if (fringe.contains(p)) {
                            fringe.changePriority(p, priority);
                        } else {
                            fringe.add(p, priority);
                        }
                        cameFrom.put(p, current);
                    }

                }

            }
        }

        Position temp;
        List<Position> path = new ArrayList<>();

        if (pNew == null) {
            pNew = start;
        }
        while (!pNew.equals(start)) {
            temp = cameFrom.get(pNew);
            path.add(temp);
            pNew = temp;
            if (pNew == null) {
                break;
            }
        }
        Collections.reverse(path);
        return path;

    }

    public void addEnemy() {
        PlayerComp c = new PlayerComp();
        List<Rectangle> startingRectChooser = new ArrayList<>(rectangles);
        startingRectChooser.sort(c);
        Rectangle startingRect = startingRectChooser.get(0);
        Position startingPos = new Position(startingRect.getLowerRight().getX() - 1,
                startingRect.getLowerRight().getY() + 1);
        enemy = new Avatar(Tileset.ENEMY, startingPos, Tileset.NEW_FLOOR);
        enemy.setPath(shortestPath(startingPos, getAvatarPos()));
    }

    public void addAvatar(Position p, TETile t) {
        avatar = new Avatar(t, p, Tileset.NEW_FLOOR);
        avatar.setPrevious(world[p.getX()][p.getY()]);
        avatar.setAvatarPos(p);
        world[p.getX()][p.getY()] = t;
    }

    public void addEnemy(Position p) {
        enemy = new Avatar(Tileset.ENEMY, p, Tileset.NEW_FLOOR);
        enemy.setAvatarPos(p);
        world[p.getX()][p.getY()] = Tileset.ENEMY;
        enemy.setPath(shortestPath(p, avatar.getAvatarPos()));
    }


    /**
     * moves Enemy to a random position
     * returns true if enemy wins
     */
    public boolean moveEnemy() {

        ArrayList<Position> optimalPath =
                new ArrayList(shortestPath(enemy.avatarPos, avatar.avatarPos));
        Position p;
        if (optimalPath.size() <= 1) { //enemy caught avatar
            p = avatar.avatarPos;
            return true;
        } else { //gives optimal position
            p = optimalPath.get(1);
            optimalPath.remove(0);
        }

        int x = p.getX();
        int y = p.getY();


        world[enemy.getAvatarPos().getX()][enemy.getAvatarPos().getY()] = enemy.getPrevious();
        enemy.setAvatarPos(new Position(x, y));
        enemy.setPrevious(world[x][y]);
        world[x][y] = enemy.getAvatarTile();

        return false;
    }


    /**
     * adds avatar to the world
     */

    public void addAvatar(TETile a) {
        Rectangle randRect = rectangles.get(genRandom(rectangles.size()));
        Position startingPos = new Position(randRect.getLowerRight().getX() - 1,
                randRect.getLowerRight().getY() + 1);
        avatar = new Avatar(a, startingPos, Tileset.NEW_FLOOR);
        avatar.setPrevious(world[randRect.getLowerRight().getX() - 1]
                [randRect.getLowerRight().getY() + 1]);
        world[randRect.getLowerRight().getX() - 1][randRect.getLowerRight().getY() + 1] =
                avatar.getAvatarTile();
        avatar.setAvatarPos(new Position(randRect.getLowerRight().getX() - 1,
                randRect.getLowerRight().getY() + 1));
    }


    /**
     * checks to see if prospect position is a valid one
     * i.e. not a wall or locked door
     *
     * @return true is position is not valid
     */
    public boolean isNotValidPosition(int x, int y) {
        if (x >= WIDTH || y >= HEIGHT) {
            return true;
        } else if (x < 0 || y < 0) {
            return true;
        }
        if (world[x][y].equals(Tileset.NEW_FLOOR) || world[x][y].equals(Tileset.UNLOCKED_DOOR)
                || world[x][y].equals(Tileset.POND)) {
            return false;
        }
        return true;
    }

    public boolean isOpenDoor() {
        Position avatarPos = avatar.getAvatarPos();
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        return (doorPosition.getY() == y && doorPosition.getX() == x);
    }

    public void checkKey() {
        if (keyPosition.getX() == avatar.getAvatarPos().getX()
                && keyPosition.getY() == avatar.getAvatarPos().getY()) {
            world[doorPosition.getX()][doorPosition.getY()] = Tileset.UNLOCKED_DOOR;
            unlockedDoor = true;
        }
    }

    /**
     * moves avatar up (if possible)
     */
    public void moveUp() {
        int xPos = avatar.getAvatarPos().getX();
        int yPos = avatar.getAvatarPos().getY();
        if (isNotValidPosition(xPos, yPos + 1)) {
            return;
        } else {
            avatar.setAvatarPos(new Position(xPos, yPos + 1));
            world[xPos][yPos] = avatar.getPrevious();
            avatar.setPrevious(world[xPos][yPos + 1]);
            world[xPos][yPos + 1] = avatar.getAvatarTile();


        }
    }

    /**
     * moves avatar left if possible
     */
    public void moveLeft() {
        int xPos = avatar.getAvatarPos().getX();
        int yPos = avatar.getAvatarPos().getY();
        if (isNotValidPosition(xPos - 1, yPos)) {
            return;
        } else {
            avatar.setAvatarPos(new Position(xPos - 1, yPos));

            world[xPos][yPos] = avatar.getPrevious();
            avatar.setPrevious(world[xPos - 1][yPos]);
            world[xPos - 1][yPos] = avatar.getAvatarTile();


        }
    }

    /**
     * moves avatar down if possible
     */
    public void moveDown() {
        int xPos = avatar.getAvatarPos().getX();
        int yPos = avatar.getAvatarPos().getY();
        if (isNotValidPosition(xPos, yPos - 1)) {
            return;
        } else {
            avatar.setAvatarPos(new Position(xPos, yPos - 1));

            world[xPos][yPos] = avatar.getPrevious();
            avatar.setPrevious(world[xPos][yPos - 1]);
            world[xPos][yPos - 1] = avatar.getAvatarTile();

        }
    }

    /**
     * moves avatar right if possible
     */
    public void moveRight() {
        int xPos = avatar.getAvatarPos().getX();
        int yPos = avatar.getAvatarPos().getY();
        if (isNotValidPosition(xPos + 1, yPos)) {
            return;
        } else {
            avatar.setAvatarPos(new Position(xPos + 1, yPos));

            world[xPos][yPos] = avatar.getPrevious();
            avatar.setPrevious(world[xPos + 1][yPos]);
            world[xPos + 1][yPos] = avatar.getAvatarTile();

        }
    }

    public void addRectangle(Position uLeft, Position lRight) {
        int uLeftX = uLeft.getX();
        int uLeftY = uLeft.getY();

        int lRightX = lRight.getX();
        int lRightY = lRight.getY();

        int height = uLeftY - lRightY;
        int width = lRightX - uLeftX;

        rectangles.add(new Rectangle(uLeft, lRight));

        drawRow(new Position(uLeftX, lRightY), width, Tileset.NEW_WALL); //bottom row
        drawRow(new Position(uLeftX, uLeftY), width + 1, Tileset.NEW_WALL); //top row

        drawCol(new Position(uLeftX, lRightY), height, Tileset.NEW_WALL);
        drawCol(new Position(lRightX, lRightY), height, Tileset.NEW_WALL);


    }

    private void fillRectangle(Position ulBound, Position lrBound) {
        for (int x = ulBound.getX() + 1; x < lrBound.getX(); x++) {
            for (int y = lrBound.getY() + 1; y < ulBound.getY(); y++) {
                world[x][y] = Tileset.NEW_FLOOR;
            }
        }
    }

    private void openDoor(Position p) {
        int x = p.getX();
        int y = p.getY();

        world[x][y] = Tileset.NEW_FLOOR;
    }

    private void connectRectangles() {
        RectComp c = new RectComp();
        List<Rectangle> copyRectangles = new ArrayList<>(rectangles);
        copyRectangles.sort(c);
        Rectangle p = copyRectangles.remove(0);
        while (copyRectangles.size() > 0) {
            connectRectHelp(p, copyRectangles.get(0));
            p = copyRectangles.remove(0);

        }
    }

    private void connectCompletelyAbove(Rectangle parent, Rectangle child) {
        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();

        if (childLowerRight.getX() < parentLowerRight.getX()) { //totally above (checked)
            Position start = new Position(childUpperLeft.getX() + 1, parentUpperLeft.getY());
            Position end = new Position(childUpperLeft.getX() + 1, childLowerRight.getY());
            openDoor(start);
            openDoor(end);
            drawVerticalHallway(start, end);
        } else { //above and to the side(checked)
            if (parentLowerRight.getX() - childUpperLeft.getX() >= 2) { //draw vertical hallway
                Position start = new Position(childUpperLeft.getX() + 1,
                        parentUpperLeft.getY());
                Position end = new Position(childUpperLeft.getX() + 1, childLowerRight.getY());
                openDoor(start);
                openDoor(end);
                drawVerticalHallway(start, end);
            } else { //draw l-shaped (checked)
                Position start = new Position(parentLowerRight.getX(),
                        parentUpperLeft.getY() - 1);
                Position end = new Position(childLowerRight.getX() - 1, childLowerRight.getY());
                openDoor(start);
                openDoor(end);
                Position turningPoint = new Position(childLowerRight.getX() - 1,
                        parentUpperLeft.getY() - 1);
                drawVerticalHallway(turningPoint, end);
                drawHorizontalHallway(start, turningPoint);
                world[childLowerRight.getX()][parentUpperLeft.getY() - 2] = Tileset.NEW_WALL;
                world[childLowerRight.getX() - 1][parentUpperLeft.getY() - 2] = Tileset.NEW_WALL;
                world[childLowerRight.getX() - 1][parentUpperLeft.getY() - 1] = Tileset.NEW_FLOOR;

            }
        }
    }

    private void connectMiddleRect(Rectangle parent, Rectangle child) {
        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();

        Position start = new Position(parentLowerRight.getX(), childUpperLeft.getY() - 1);
        Position end = new Position(childUpperLeft.getX(), childUpperLeft.getY() - 1);
        openDoor(start);
        openDoor(end);
        drawHorizontalHallway(start, end);
    }

    private void connectCompletelyBelow(Rectangle parent, Rectangle child) {
        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();

        if (childLowerRight.getX() < parentLowerRight.getX()) { //completely below (checked)
            Position start = new Position(childUpperLeft.getX() + 1, childUpperLeft.getY());
            Position end = new Position(childUpperLeft.getX() + 1, parentLowerRight.getY());
            openDoor(start);
            openDoor(end);
            drawVerticalHallway(start, end);
        } else {
            if (parentLowerRight.getX() - childUpperLeft.getX() >= 2) {
                //below overlap (checked)
                Position start = new Position(childUpperLeft.getX() + 1, childUpperLeft.getY());
                Position end = new Position(childUpperLeft.getX() + 1, parentLowerRight.getY());
                openDoor(start);
                openDoor(end);
                drawVerticalHallway(start, end);
            } else { //below partial overlap (checked)
                Position start = new Position(parentLowerRight.getX(),
                        parentLowerRight.getY() + 1);
                Position end = new Position(childLowerRight.getX() - 1, childUpperLeft.getY());

                openDoor(start);
                openDoor(end);
                Position turningPoint = new Position(childLowerRight.getX() - 1,
                        parentLowerRight.getY() + 1);
                drawHorizontalHallway(start, turningPoint);
                drawVerticalHallway(end, turningPoint);
                world[childLowerRight.getX()][parentLowerRight.getY() + 1] = Tileset.NEW_WALL;
                world[childLowerRight.getX()][parentLowerRight.getY() + 2] = Tileset.NEW_WALL;
                world[childLowerRight.getX() - 1][parentLowerRight.getY() + 2] = Tileset.NEW_WALL;
                world[childLowerRight.getX() - 1][parentLowerRight.getY() + 1] = Tileset.NEW_FLOOR;

            }
        }
    }

    private void connectRectPartialAbove(Rectangle parent, Rectangle child) {
        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();
        if (parentUpperLeft.getY() - childLowerRight.getY() <= 2) { //partial above LH(checked)
            Position start = new Position(parentLowerRight.getX(), parentLowerRight.getY() + 1);
            Position end = new Position(childUpperLeft.getX() + 1, childLowerRight.getY());
            openDoor(start);
            openDoor(end);

            Position turningPoint = new Position(childUpperLeft.getX() + 1,
                    parentLowerRight.getY() + 1);
            drawVerticalHallway(turningPoint, end);
            drawHorizontalHallway(start, turningPoint);
            world[childUpperLeft.getX() + 1][parentLowerRight.getY()] = Tileset.NEW_WALL;
            world[childUpperLeft.getX() + 2][parentLowerRight.getY()] = Tileset.NEW_WALL;
            world[childUpperLeft.getX()][parentLowerRight.getY()] = Tileset.NEW_FLOOR;


            //drawLShapedHallway(start, end);
        } else { //partial above with enough space for horizontal (checked)
            Position start = new Position(parentLowerRight.getX(), parentUpperLeft.getY() - 1);
            Position end = new Position(childUpperLeft.getX(), parentUpperLeft.getY() - 1);
            openDoor(start);
            openDoor(end);
            drawHorizontalHallway(start, end);
        }
    }

    private void connectRectPartialBelow(Rectangle parent, Rectangle child) {

        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();
        if (childUpperLeft.getY() - parentLowerRight.getY() <= 2) { //l-shaped hallway
            Position start = new Position(parentLowerRight.getX(), parentUpperLeft.getY() - 1);
            Position end = new Position(childUpperLeft.getX() + 1, childUpperLeft.getY());

            openDoor(start);
            openDoor(end);

            Position turningPoint = new Position(childUpperLeft.getX() + 1,
                    parentUpperLeft.getY() - 1);
            drawVerticalHallway(end, turningPoint);
            drawHorizontalHallway(start, turningPoint);
            world[childUpperLeft.getX() + 1][parentUpperLeft.getY()] = Tileset.NEW_WALL;
            world[childUpperLeft.getX() + 2][parentUpperLeft.getY()] = Tileset.NEW_WALL;
            world[childUpperLeft.getX() + 2][parentUpperLeft.getY() - 1] = Tileset.NEW_WALL;
            world[childUpperLeft.getX() + 1][parentUpperLeft.getY() - 1] = Tileset.NEW_FLOOR;


        } else { //horizontal (checked)
            Position start = new Position(parentLowerRight.getX(), childUpperLeft.getY() - 1);
            Position end = new Position(childUpperLeft.getX(), childUpperLeft.getY() - 1);
            openDoor(start);
            openDoor(end);
            drawHorizontalHallway(start, end);
        }
    }

    private void connectBigRect(Rectangle parent, Rectangle child) {

        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();
        Position start = new Position(parentLowerRight.getX(), parentLowerRight.getY() + 1);
        Position end = new Position(childUpperLeft.getX(), parentLowerRight.getY() + 1);
        openDoor(start);
        openDoor(end);
        drawHorizontalHallway(start, end);

    }

    private void connectRectHelp(Rectangle parent, Rectangle child) {
        Position parentUpperLeft = parent.getUpperLeft();
        Position parentLowerRight = parent.getLowerRight();

        Position childUpperLeft = child.getUpperLeft();
        Position childLowerRight = child.getLowerRight();

        if (childLowerRight.getY() >= parentUpperLeft.getY()) { // child is completely above parent
            connectCompletelyAbove(parent, child);
        } else if (childUpperLeft.getY() <= parentUpperLeft.getY()
                && childLowerRight.getY() >= parentLowerRight.getY()) { //middle area (checked)
            connectMiddleRect(parent, child);

        } else if (childUpperLeft.getY() < parentLowerRight.getY()) { //completely below
            connectCompletelyBelow(parent, child);

        } else if (childUpperLeft.getY() >= parentUpperLeft.getY()
                && childLowerRight.getY() >= parentLowerRight.getY()) {
            // overlap above
            connectRectPartialAbove(parent, child);

        } else if (childUpperLeft.getY() >= parentLowerRight.getY()
                && childLowerRight.getY() <= parentLowerRight.getY()
                && childUpperLeft.getY() < parentUpperLeft.getY()) {
            //overlap below
            connectRectPartialBelow(parent, child);

        } else { //bigger rectangle
            connectBigRect(parent, child);
        }

    }

    /**
     * draws a hallway between the provided positions
     */

    private void drawVerticalHallway(Position start, Position end) {
        Position leftWallStart = new Position(start.getX() - 1, start.getY());
        Position leftWallEnd = new Position(end.getX() - 1, end.getY());

        Position rightWallStart = new Position(start.getX() + 1, start.getY());
        Position rightWallEnd = new Position(end.getX() + 1, end.getY());

        drawCol(leftWallStart, leftWallEnd.getY() - leftWallStart.getY(), Tileset.NEW_WALL);
        drawCol(rightWallStart, rightWallEnd.getY() - rightWallStart.getY(), Tileset.NEW_WALL);
        drawCol(start, end.getY() - start.getY(), Tileset.NEW_FLOOR);

    }

    private void drawHorizontalHallway(Position start, Position end) {
        Position topWallStart = new Position(start.getX(), start.getY() + 1);
        Position topWallEnd = new Position(end.getX(), end.getY() + 1);

        Position bottomWallStart = new Position(start.getX(), start.getY() - 1);
        Position bottomWallEnd = new Position(end.getX(), end.getY() - 1);

        drawRow(bottomWallStart, bottomWallEnd.getX() - bottomWallStart.getX(), Tileset.NEW_WALL);
        drawRow(topWallStart, topWallEnd.getX() - topWallStart.getX(), Tileset.NEW_WALL);
        drawRow(start, end.getX() - start.getX(), Tileset.NEW_FLOOR);
    }

    public void rectangleWorld() {
        int ulX = genRandom(WIDTH);
        int lrX = genRandom(WIDTH);
        if (ulX == lrX) {
            ulX = genRandom(WIDTH);
        }
        if (ulX > lrX) {
            int temp = ulX;
            ulX = lrX;
            lrX = temp;
        }

        int ulY = genRandom(HEIGHT);
        int lrY = genRandom(HEIGHT);
        if (lrY == ulY) {
            ulY = genRandom(HEIGHT);
        }
        if (lrY > ulY) {
            int temp2 = ulY;
            ulY = lrY;
            lrY = temp2;
        }
        Position ulCorner = new Position(ulX, ulY);
        Position lrCorner = new Position(lrX, lrY);

        addRectangle(ulCorner, lrCorner); // add first rectangle


        Position topUL = new Position(ulCorner.getX(), HEIGHT);
        //positions of corners of new worlds to draw rects recursively
        Position topLR = new Position(WIDTH, ulCorner.getY());

        Position rightUL = new Position(lrCorner.getX(), ulCorner.getY());
        Position rightLR = new Position(WIDTH, 0);

        Position bottomUL = new Position(0, lrCorner.getY());
        Position bottomLR = new Position(lrCorner.getX(), 0);

        Position leftUL = new Position(0, HEIGHT);
        Position leftLR = new Position(ulCorner.getX(), lrCorner.getY());

        rectangleWorldHelper(topUL, topLR); // first area
        rectangleWorldHelper(rightUL, rightLR);
        rectangleWorldHelper(bottomUL, bottomLR);
        rectangleWorldHelper(leftUL, leftLR);

        List<Rectangle> copyRectangles = new ArrayList<>(rectangles);
        connectRectangles();
        for (Rectangle r : copyRectangles) {
            fillRectangle(r.upperLeft, r.lowerRight);
        }

    }

    /**
     * This method will draw a rectangle in the specified bounds
     *
     * @param ulBound - upper left bound of available area
     * @param lrBound - lower right bound of available area
     *                position start and end determine range of where the next room can start
     */
    public void rectangleWorldHelper(Position ulBound, Position lrBound) {
        int xDist = lrBound.getX() - ulBound.getX();
        int yDist = ulBound.getY() - lrBound.getY();

        if (xDist < 4 || yDist < 4) { //base case if space is not enough to fit a rectangle
            return;
        }

        int newWidth = xDist;
        int newHeight = yDist;


        int ulX = genRandom(newWidth) + ulBound.getX();
        int lrX = genRandom(newWidth) + ulBound.getX();
        if (ulX == lrX) {
            ulX = genRandom(newWidth) + ulBound.getX();
        }
        while (lrX - ulX < 3 || lrX - ulX > 16) {
            ulX = genRandom(newWidth) + ulBound.getX();
            lrX = genRandom(newWidth) + ulBound.getX();
        }

        int ulY = genRandom(newHeight) + lrBound.getY();
        int lrY = genRandom(newHeight) + lrBound.getY();
        if (lrY == ulY) {
            ulY = genRandom(newHeight) + lrBound.getY();
        }
        while (ulY - lrY < 3 || ulY - lrY > 16) {
            ulY = genRandom(newHeight) + lrBound.getY();
            lrY = genRandom(newHeight) + lrBound.getY();
        }
        Position ulCorner = new Position(ulX, ulY);
        Position lrCorner = new Position(lrX, lrY);

        addRectangle(ulCorner, lrCorner); // draws rectangle


        Position topUL = new Position(ulCorner.getX() + 1, ulBound.getY() - 1);
        //positions of corners of new worlds to draw rects recursively
        Position topLR = new Position(lrBound.getX(), ulCorner.getY());

        Position rightUL = new Position(lrCorner.getX() + 1, ulCorner.getY() - 1);
        Position rightLR = new Position(lrBound.getX(), lrBound.getY() + 1);

        Position bottomUL = new Position(ulBound.getX() + 1, lrCorner.getY() - 1);
        Position bottomLR = new Position(lrCorner.getX(), lrBound.getY());

        Position leftUL = new Position(ulBound.getX() + 1, ulBound.getY() - 1);
        Position leftLR = new Position(ulCorner.getX(), lrCorner.getY());

        rectangleWorldHelper(topUL, topLR); // first area

        rectangleWorldHelper(rightUL, rightLR);
        rectangleWorldHelper(bottomUL, bottomLR);
        rectangleWorldHelper(leftUL, leftLR);

    }


    private int genRandom(int upperBound) {
        return rand.nextInt(upperBound);
    }

    private void drawRow(Position start, int length, TETile tileType) {
        int xX = start.getX();
        int yY = start.getY();

        for (int i = xX; i < length + xX; i++) {
            if (!world[i][yY].equals(Tileset.NEW_FLOOR)) {
                world[i][yY] = tileType;

            }
        }
    }

    private void drawCol(Position start, int length, TETile tileType) {
        int xX = start.getX();
        int yY = start.getY();

        for (int i = yY; i < length + yY; i++) {
            if (!world[xX][i].equals(Tileset.NEW_FLOOR)) {
                world[xX][i] = tileType;
            }
        }
    }

    public TETile[][] getFrame() {
        return world;
    }

    public class Avatar implements Serializable {
        Position avatarPos;
        Position startPos;
        TETile avatarTile;
        TETile previous;
        List<Position> path;
        List<Position> secondPath;

        public Avatar(TETile avatar, Position pos, TETile previousTile) {
            avatarTile = avatar;
            avatarPos = pos;
            previous = previousTile;
            startPos = pos;

        }

        public TETile getPrevious() {
            return previous;
        }

        public void setPrevious(TETile p) {
            previous = p;
        }

        public Position getAvatarPos() {
            return avatarPos;
        }

        public TETile getAvatarTile() {
            return avatarTile;
        }

        public void setAvatarPos(Position p) {
            avatarPos = p;
        }

        public void setPath(List<Position> l) {
            path = l;
            secondPath = new ArrayList<>(l);
        }
    }

    public class Rectangle implements Serializable {

        private Position upperLeft;
        private Position lowerRight;

        public Rectangle(Position ul, Position lr) {
            upperLeft = ul;
            lowerRight = lr;
        }

        public Position getUpperLeft() {
            return upperLeft;
        }

        public Position getLowerRight() {
            return lowerRight;
        }
    }

    public class RectComp implements Comparator<Rectangle> {
        public int compare(Rectangle T, Rectangle W) {
            return T.getUpperLeft().getX() - W.getUpperLeft().getX();
        }
    }
}
