import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public abstract class Brick extends GameObj {
	private static final int WIDTH = 30;
	private static final int HEIGHT = 15;
	private static final int VEL = 0;

	protected int code;
	protected int hits;
	protected boolean hasBeenHit;
	protected Color color;

	// bomb fields
	private boolean isBomb;
	private static BufferedImage img;
	private static long timeOfBomb;
	//todo empty
	public Brick (int x, int y, int courtWidth, int courtHeight) {
		super(VEL, VEL, x, y, WIDTH, HEIGHT, courtWidth, courtHeight);
		hasBeenHit = false;
		isBomb = false;
	}

	public BrickType getBrickType() {
		if (code == 0) {
			return BrickType.EMPTY;
		}
		else if (code >= 1 && code <= 4) {
			return BrickType.REGULAR;
		}
		else if (code <= 8) {
			return BrickType.BOMB;
		}
		else {
			return BrickType.UNBREAKABLE;
		}
	}

	//getters
	public int getCode() {
		return code;
	}
	
	public int getHits() {
		return hits;
	}

	public boolean getHasBeenHit() {
		return hasBeenHit;
	}

	//setters
	public void registerHit() {
		hits--;
		hasBeenHit = true;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	//make brick empty
	public void makeEmpty(boolean isBomb) {
		code = 0;
		hits = 0;
		hasBeenHit = false;
		color = Color.BLACK;
		this.isBomb = isBomb;
	}

	//bomb methods
	public static long getTimeOfBomb() { return timeOfBomb; }

	public void setIsBombFalse() {
		isBomb = false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Brick)) {
			return false;
		}
		Brick that = (Brick) o;
		return this.code == that.code;
	}
	
	@Override
    public void draw(Graphics g) {
		int x = getPx();
		int y = getPy();
		int width = getWidth();
		int height = getHeight();
		g.setColor(color);
        g.fillRect(x, y, width, height);

        // draw bomb explosions
		if (isBomb) {
			String IMG_FILE = "files/pow.png";
			try {
				if (img == null) {
					img = ImageIO.read(new File(IMG_FILE));
				}
			} catch (IOException e) {
				System.out.println("Internal Error:" + e.getMessage());
			}
			g.drawImage(img, x, y, width, height, null);
			timeOfBomb = System.currentTimeMillis();
		}

		// draw borders
		g.setColor(Color.BLACK);
		if (!(code == 0)) {
			g.drawRect(x, y, width, height);
		}

		/* If Regular (but not code == 1) or Bomb brick that has been hit, 
		 * draw cracks for 3, 2 and 1 hits left */
		if (code >= 2 && code <= 8 && hasBeenHit) {
			if (hits <= 3) {
				g.drawLine(x + 10, y, x + 12, y + 3);
				g.drawLine(x + 12, y + 3, x + 14, y + 6);
				g.drawLine(x + 12, y + 3, x + 7, y + 4);
				g.drawLine(x + 7, y + 4, x + 9, y + 6);
			}
			if (hits <= 2) {
				g.drawLine(x + 3, y + 15, x + 4, y + 11);
				g.drawLine(x + 4, y + 11, x + 7, y + 9);
				g.drawLine(x + 28, y + 15, x + 25, y + 12);
				g.drawLine(x + 25, y + 12, x + 20, y + 10);
				g.drawLine(x + 25, y + 12, x + 27, y + 11);
				g.drawLine(x + 27, y + 11, x + 28, y + 9);
				g.drawLine(x + 25, y + 12, x + 27, y + 13);
				g.drawLine(x + 27, y + 13, x + 29, y + 14);
			}
			if (hits == 1) {
				g.drawLine(x + 27, y, x + 25, y + 4);
				g.drawLine(x + 25, y + 4, x + 23, y + 7);
				g.drawLine(x + 14, y + 15, x + 16, y + 12);
				g.drawLine(x + 16, y + 12, x + 11, y + 10);
				g.drawLine(x + 6, y, x + 8, y + 2);
				g.drawLine(x + 8, y + 2, x + 5, y + 7);
			}
		} 
    }
}

class RegularBrick extends Brick {
	public RegularBrick(int code, int x, int y, Color color, int courtWidth, int courtHeight, boolean hasBeenHit) {
		super(x, y, courtWidth, courtHeight);
		this.code = code;
		if (code >= 1 && code <= 4) {
			hits = code;
		}
		else {
			throw new IllegalArgumentException("Illegal code");
		}
		this.color = color;
		this.hasBeenHit = hasBeenHit;
	}
}

class BombBrick extends Brick {
	public BombBrick(int code, int x, int y, Color color, int courtWidth, int courtHeight, boolean hasBeenHit) {
		super(x, y, courtWidth, courtHeight);
		this.code = code;
		if (code >= 5 && code <= 8) {
			hits = code - 4;
		}
		else {
			throw new IllegalArgumentException("Illegal code");
		}
		this.color = color;
		this.hasBeenHit = hasBeenHit;
	}
}

// Unbreakable bricks only break if bombed
class UnbreakableBrick extends Brick {
	public UnbreakableBrick(int x, int y, int courtWidth, int courtHeight) {
		super(x, y, courtWidth, courtHeight);
		code = 9;
		hits = 1; // this will never change, since registerHit() is overridden
		color = Color.GRAY;
		hasBeenHit = false;
	}

	@Override
	public void registerHit() {
		// for unbreakable brick, a hit does nothing
	}
}