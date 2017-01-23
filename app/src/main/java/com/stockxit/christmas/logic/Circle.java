package com.stockxit.christmas.logic;

/**
 * Created by Thomas on 23/01/2017.
 */

public class Circle {
    private int x;
    private int y;
    private int r;
    private int color;

    public Circle(int x, int y, int r, int color) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getR() { return r; }
    public int getColor() { return color; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setR(int r) { this.r = r ; }
    public void setColor(int color) { this.color = color; }

    @Override
    public String toString() {
        return "Circle[" + x + ", " + y + ", " + r + ", " + color + "]";
    }
}
