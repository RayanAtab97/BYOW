package byow.OurWorld;

import java.io.Serializable;

public class Position implements Serializable {
    private int x;
    private int y;


    public Position(int x1, int y1) {
        x = x1;
        y = y1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object a) {
        Position b = (Position) a;
        if (a == this) {
            return true;
        } else if (!(a instanceof Position)) {
            return false;
        }
        return Integer.compare(b.getX(), this.x) == 0 && Integer.compare(b.getY(), this.y) == 0;
    }

    //
    @Override
    public int hashCode() {
        int m = (int) (this.getX() * Math.pow(31, 2) + this.getY() * 31);
        int base = (int) Math.pow(31, 2) * 100;
        return Math.floorMod(m, base);
    }

    public static boolean equals(Position a, Position b) {
        if (a == null || b == null) {
            return false;
        }
        boolean x = a.getX() == b.getX();
        boolean y = a.getY() == b.getY();
        return x && y;
    }
}
