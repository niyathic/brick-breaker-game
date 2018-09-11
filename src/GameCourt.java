/**Skeleton (c) University of Pennsylvania - CIS 120 Game HW
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact with one another. Take
 * time to understand how the timer interacts with the different methods and how it repaints the GUI
 * on every tick().
 */
@SuppressWarnings("serial")

public class GameCourt extends JPanel {

    /*
     * The state of the game logic
     */

    // Objects
	private Ball ball; // the ball, bounces off walls and paddle, and hits bricks
	private Paddle paddle; // paddle to hit ball, keyboard control
	private Brick[][] brickArray; // array of bricks coded by type/presence

    // Labels
    private JLabel livesLabel; // labels for remaining lives, decremented when ball hits south border
    private JLabel score1Label; // labels for top 3 scores
    private JLabel score2Label;
    private JLabel score3Label;

    // Saved info
    private boolean playing = false; // whether the game is running
	private int lives; // decremented when the ball hits the south border
    private int num; // number of bricks hit or broken, used to calculate score
	private boolean gameEnd = false; // whether game has ended (won or lost)
	private String name; // current player's username
    private long loadedTime = 0; // elapsed time from load()

	// Structures
    private Stopwatch stopwatch = new Stopwatch(); // used to get time information to calculate score
    private List<UserScore> topScores = new LinkedList<>(); // list of UserScores

	// Music and sound effects
	private Clip music;
	private Clip hit;
    private Clip broken;
    private Clip bomb;
    private Clip lose;
    private Clip win;

    // Game constants
    private static final int COURT_WIDTH = 390;
    private static final int COURT_HEIGHT = 300;
    private static final int PADDLE_VELOCITY = 7;
    private static final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
            Color.CYAN, Color.BLUE, Color.MAGENTA, Color.PINK};
    private static final int NUM_SCORES = 3;
    // Update interval for timer, in milliseconds
    private static final int INTERVAL = 35;

    public GameCourt(JLabel livesLabel, JLabel score1Label,
                     JLabel score2Label, JLabel score3Label, String name) throws
            javax.sound.sampled.LineUnavailableException,
            javax.sound.sampled.UnsupportedAudioFileException, IOException {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // The timer is an object which triggers an action periodically with the given INTERVAL. We
        // register an ActionListener with this timer, whose actionPerformed() method is called each
        // time the timer triggers. We define a helper method called tick() that actually does
        // everything that should be done in a single timestep.

        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	tick();
            }
        });
        timer.start();

        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // set up music and sound effects
        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream
                (new File("files/bgmusic.wav").getAbsoluteFile());
        music = AudioSystem.getClip();
        music.open(audioInputStream1);

        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream
                (new File("files/hit.wav").getAbsoluteFile());
        hit = AudioSystem.getClip();
        hit.open(audioInputStream2);

        AudioInputStream audioInputStream3 = AudioSystem.getAudioInputStream
                (new File("files/break.wav").getAbsoluteFile());
        broken = AudioSystem.getClip();
        broken.open(audioInputStream3);

        AudioInputStream audioInputStream4 = AudioSystem.getAudioInputStream
                (new File("files/bomb.wav").getAbsoluteFile());
        bomb = AudioSystem.getClip();
        bomb.open(audioInputStream4);

        AudioInputStream audioInputStream5 = AudioSystem.getAudioInputStream
                (new File("files/lose.wav").getAbsoluteFile());
        lose = AudioSystem.getClip();
        lose.open(audioInputStream5);

        AudioInputStream audioInputStream6 = AudioSystem.getAudioInputStream
                (new File("files/win.wav").getAbsoluteFile());
        win = AudioSystem.getClip();
        win.open(audioInputStream6);

        // This key listener allows the square to move as long as an arrow key is pressed, by
        // changing the paddle's position accordingly. (The tick method below actually moves the
        // paddle.)

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    paddle.setVx(-PADDLE_VELOCITY);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    paddle.setVx(PADDLE_VELOCITY);
                }
            }

            public void keyReleased(KeyEvent e) {
                paddle.setVx(0);
                paddle.setVy(0);
            }

        });

		this.livesLabel = livesLabel;
		this.score1Label = score1Label;
		this.score2Label = score2Label;
		this.score3Label = score3Label;
        this.name = name;
    }


	private int getScore() {
		/* Calculate score as function of number of bricks broken/hit and time elapsed.
		   Number of bricks hit is a greater determinant of score than time elapsed;
		   time elapsed is mainly a way to differentiate between game win scores
		 */
		double sec = (stopwatch.elapsed() + loadedTime) / 1000000000;
        double min = sec / 60;
        double minScaled = Math.pow(min, 0.1);
		int score = (int) (Math.pow(num, 1.1) / minScaled / 10);

		// update top scores at game end
        if (gameEnd) {
            // add 100 points for game win
            if (lives > 0) {
                score += 100;
            }
            // update score list
            topScores.add(new UserScore(score, name));
            Collections.sort(topScores);
            if (topScores.size() > NUM_SCORES) {
                topScores.remove(0);
            }
        }
		return score; 
	}

	private void displayScores() {
        String[] res = new String[NUM_SCORES];
        // display 0's where scores do not exist
        for (int i = topScores.size(); i < NUM_SCORES; i++) {
            res[i] = "0";
        }
        // display existing scores
        for (int i = 0; i < topScores.size(); i++) {
            UserScore user = topScores.get(i);
            res[i] = user.getName() + ": " + user.getScore();
        }
        score1Label.setText(res[0]);
        score2Label.setText(res[1]);
        score3Label.setText(res[2]);
    }


	private Brick makeBrick(int i, int j, int code, boolean hasBeenHit) {
		switch (code) {
			case 0:
				Brick brick = new RegularBrick(1, i * 30, j * 15, colors[j],
						COURT_WIDTH, COURT_HEIGHT, hasBeenHit);
				brick.makeEmpty(false);
				return brick;
			case 1: case 2: case 3: case 4:
				return new RegularBrick(code, i * 30, j * 15, colors[j],
						COURT_WIDTH, COURT_HEIGHT, hasBeenHit);
			case 5: case 6: case 7: case 8:
				return new BombBrick(code, i * 30, j * 15, colors[j],
						COURT_WIDTH, COURT_HEIGHT, hasBeenHit);
			case 9:
				return new UnbreakableBrick(i * 30, j * 15, COURT_WIDTH, COURT_HEIGHT);
		}
		return null;
	}


	// reset game to its initial state
    public void reset() {
    	pause();
    	stopwatch.stop();
    	gameEnd = false;
		ball = new Ball(COURT_WIDTH, COURT_HEIGHT, Color.WHITE);
		paddle = new Paddle(COURT_WIDTH, COURT_HEIGHT, Color.CYAN);
		lives = 3;
		num = 0;
		name = Game.getName();
		loadedTime = 0;

		// generate brick array
		brickArray = new Brick[13][8];
		for (int i = 0; i < brickArray.length; i++) {
			for (int j = 0; j < brickArray[0].length; j++) {
				// generate random codeMakers, with certain bricks being more likely
				int codeMaker = (int) (Math.random() * 31 + 1);
				int code;
				if (codeMaker == 0) {
					code = 0;
				} else if (codeMaker <= 10) {
					code = 1;
				} else if (codeMaker <= 25) {
					code = (codeMaker - 11)/5 + 2;
				} else if (codeMaker <= 29) {
					code = codeMaker - 21;
				} else {
					code = 9;
				}
				brickArray[i][j] = makeBrick(i, j, code, false);
			}
		}
		livesLabel.setText("Lives: " + lives);
		displayScores();
		repaint();
    }
	
	// after pressing start button, start game
	public void start() {
        if (gameEnd) {
            reset();
        }
		if (!music.isRunning()) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
        if (stopwatch.isPaused()) {
            stopwatch.resume();
        }
        else if (!stopwatch.isRunning()) {
            stopwatch.start();
        }
        playing = true;
		// make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	public void pause() {
		playing = false;
		stopwatch.pause();
        music.stop();
	}

	public void save() throws IOException {
		BufferedWriter saved = new BufferedWriter(new FileWriter("files/saved.txt"));
		saved.write(Integer.toString((int) (stopwatch.elapsed() - 21450000000L)));
		saved.write("," + Integer.toString(lives));
		saved.write("," + Integer.toString(ball.getPx()));
		saved.write("," + Integer.toString(ball.getPy()));
		saved.write("," + Integer.toString(paddle.getPx()));
		saved.write("," + Integer.toString(paddle.getPy()));
        for (UserScore user : topScores) {
            saved.write("," + user.getScore());
            saved.write("," + stringToInt(user.getName()));
		}
		//add 0's
		for (int i = topScores.size(); i < 3; i++) {
            saved.write("," + 0);
            saved.write("," + stringToInt("a"));
        }
		for (int i = 0; i < brickArray.length; i++) {
			for (int j = 0; j < brickArray[0].length; j++) {
				Brick brick = brickArray[i][j];
				saved.write("," + Integer.toString(brick.getCode()));
				saved.write("," + Integer.toString(brick.getHits()));
                int hasBeenHitInt = 0;
                if (brick.getHasBeenHit()) {
                    hasBeenHitInt = 1;
                }
				saved.write("," + Integer.toString(hasBeenHitInt));
			}
		}
		pause();
		saved.close();
	}

	public void load() throws IOException {
		pause();

		// read from saved file
		InfoReader r = new InfoReader("files/saved.txt");
        int[] saved = r.getInfo();

        if (saved != null) {
            // get old elapsed time, reset current
            loadedTime = saved[0] + 21450000000L;
            stopwatch.stop();

            // update game info
            lives = saved[1];
            ball.setPx(saved[2]);
            ball.setPy(saved[3]);
            paddle.setPx(saved[4]);
            paddle.setPy(saved[5]);
            List<UserScore> savedScores = new LinkedList<>();
            for (int i = 6; i < 11; i += 2) {
                if (saved[i] != 0) {
                    savedScores.add(new UserScore(saved[i], intToString(saved[i + 1])));
                }
            }
            topScores = savedScores;
            displayScores();
            int num = 12;
            // read brickArray information
            boolean allEmpty = true;
            for (int i = 0; i < 13; i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.println(num);
                    /*
                    For each brick, saved has 3 pieces of information:
                    1. Code
                    2. Hits
                    3. HasBeenHit
                     */
                    boolean hasBeenHit = false;
                    if (saved[num + 2] == 1) {
                        hasBeenHit = true;
                    }
                    brickArray[i][j] = makeBrick(i, j, saved[num], hasBeenHit);
                    brickArray[i][j].setHits(saved[num + 1]);
                    if (saved[num] != 0 && saved[num] != 9) {
                        allEmpty = false;
                    }
                    num += 3;
                }
            }

            // see if saved game was a lost game
            if (lives == 0) {
                gameEnd = true;
                playing = false;
                livesLabel.setText("Loaded lost game! Press start or reset.");
            }
            // see if saved game was a won game
            else if (allEmpty) {
                gameEnd = true;
                playing = false;
                livesLabel.setText("Loaded won game! Press start or reset.");
            } else {
                livesLabel.setText("Lives: " + lives);
                gameEnd = false;
            }
            repaint();
        }
	}

	/*
	 * Helper methods to convert top scorers' names to and from ints
	 */

	//
    private int stringToInt(String s) {
        String rep = "";
        for (int i = 0; i < s.length(); i++) {
            int curr = s.charAt(i) - 'a' + 1;
            if (curr < 10) {
                rep += "0";
            }
            rep += curr;
        }
        return Integer.parseInt(rep);
    }

    private String intToString(int i) {
        String rep = Integer.toString(i);
        if (rep.length() % 2 != 0) {
            rep = "0" + rep;
        }
        String res = "";
        for (int j = 0; j < rep.length() - 1; j += 2) {
            char[] chars = {rep.charAt(j), rep.charAt(j + 1)};
            char curr = (char) (Integer.parseInt(new String(chars)) + 'a' - 1);
            int inty = Integer.parseInt(new String(chars)) + 64;
            res += curr;
        }
        return res;
    }

    /**
     * This method is called every time the timer defined in the constructor triggers.
     */
    void tick() {
        if (playing) {
            // advance the ball and paddle in their current direction to start
			paddle.move();
			ball.move();

            // make the ball bounce off the paddle
            if (ball.willIntersect(paddle)) {
            	ball.bounce(Direction.DOWN);
			}

			// make ball bounce on walls, except bottom
            Direction d = ball.hitWall();
			if (d != null && d.equals(Direction.DOWN)) {
				lives--;
				// restart ball above paddle
				ball.setPx(paddle.getPx() + 40);
				ball.setPy(283);
				pause();
				livesLabel.setText("<html>Lives: " + lives);
				// check for game end condition
				if (lives == 0) {
				    gameEnd = true;
					livesLabel.setText("<html>You lose! Score: " + getScore());
                    displayScores();
					playing = false;
					// play music
                    lose.setFramePosition(0);
                    lose.start();
				}
			}
			else if (d != null) {
				ball.bounce(d);
			}
			
			// check for ball hitting brick and update conditions
			boolean allEmpty = true;
			
			for (int i = 0; i < brickArray.length; i++) {
				for (int j = 0; j < brickArray[0].length; j++) {
					Brick brick = brickArray[i][j];
					if (brick.getCode() != 0 && brick.getCode() != 9) {
						allEmpty = false;
					}
					if (ball.willIntersect(brick)) {
						if (brick.getBrickType() == BrickType.REGULAR) {
							ball.bounce(ball.hitObj(brick));
							int hits = brick.getHits();
							if (hits > 1) {
								brick.registerHit();
                                num += 5;
                                // play sound
								hit.setFramePosition(0);
								hit.start();
							}
							else {
								brick.makeEmpty(false);
                                num += 20;
								// play sound
								broken.setFramePosition(0);
								broken.start();
							}
						}
						else if (brick.getBrickType() == BrickType.BOMB) {
							ball.bounce(ball.hitObj(brick));
							int hits = brick.getHits();
							if (hits > 1) {
								brick.registerHit();
                                num += 5;
								// play sound
								hit.setFramePosition(0);
                                hit.start();
							}
							else {
                                int numBricksInBomb = 0;
								for (int a = Math.max(0, i - 1); a <= Math.min(12, i + 1); a++) {
									for (int b = Math.max(0, j - 1); b <= Math.min(7, j + 1); b++) {
									    Brick brick2 = brickArray[a][b];
                                        if (brick2.getCode() != 0) {
									        numBricksInBomb++;
                                            brick2.makeEmpty(true);
                                        }
									}
								}
                                num += 20 * numBricksInBomb;
								// play sound
                                bomb.setFramePosition(0);
                                bomb.start();
							}
						}
						else if (brick.getBrickType() == BrickType.UNBREAKABLE) {
							ball.bounce(ball.hitObj(brick));
							// play sound
							broken.setFramePosition(0);
                            broken.start();
						}
						// if it is an empty brick, the ball will just pass through
					}
				}
			}
			
			// if all bricks are empty or unbreakable, game is won
			if (allEmpty) {
				livesLabel.setText("<html>You win! Score: " + getScore());
				displayScores();
				gameEnd = true;
                playing = false;
                // play winning music
                music.stop();
                win.setFramePosition(0);
				win.start();
			}

            // update the display
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //draw black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);

        /*
         * Draw objects
         */

		//set length of appearance of bomb explosion graphic
		boolean appeared = System.currentTimeMillis() - Brick.getTimeOfBomb() == 35;
		for (int i = 0; i < brickArray.length; i++) {
			for (int j = 0; j < brickArray[0].length; j++) {
				brickArray[i][j].draw(g);
				if (appeared) {
					brickArray[i][j].setIsBombFalse();
				}
			}
		}
        paddle.draw(g);
        ball.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}