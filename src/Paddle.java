import java.awt.*;

public class Paddle extends GameObj {
	private static final int WIDTH = 90;
    private static final int HEIGHT = 6;
    private static final int INIT_POS_X = 150;
    private static final int INIT_POS_Y = 294;
    private static final int INIT_VEL_X = 0;
    private static final int INIT_VEL_Y = 0;
	
	private Color color;
	
	public Paddle(int courtWidth, int courtHeight, Color color) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, WIDTH, HEIGHT, courtWidth, courtHeight);
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
    }
}