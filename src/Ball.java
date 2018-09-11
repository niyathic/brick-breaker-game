/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;

/**
 * A basic game object starting in the upper left corner of the game court. It is displayed as a
 * circle of a specified color.
 */
public class Ball extends GameObj {
    private static final int SIZE = 10;
    private static final int POS_X = 190;
    private static final int POS_Y = 283;
    private static final int VEL = 6;

    private Color color;

    public Ball(int courtWidth, int courtHeight, Color color) {
        super(VEL, VEL, POS_X, POS_Y, SIZE, SIZE, courtWidth, courtHeight);

        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillOval(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
    }
}