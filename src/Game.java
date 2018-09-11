/**
 * Skeleton (c) University of Pennsylvania - CIS 120 Game Homework
 * @version 2.1, Apr 2017

 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
    // Username
    private static String name;

    public static String getName() {
        return name;
    }
    public void run() throws IllegalArgumentException {
        // NOTE : recall that the 'final' keyword notes immutability even for local variables.

        // Top-level frame in which game components live
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("TOP LEVEL FRAME");
        frame.setLocation(300, 300);

        // Status panel
        GridLayout status_grid = new GridLayout(0,1);
        final JPanel status_panel = new JPanel();
        status_panel.setLayout(status_grid);
        frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel livesLabel = new JLabel("Lives: 3");
		livesLabel.setHorizontalAlignment(JLabel.CENTER);
		status_panel.add(livesLabel);
        final JLabel scoreLabel = new JLabel("Top scores:");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        status_panel.add(scoreLabel);
        final JLabel score1Label = new JLabel("0");
        score1Label.setHorizontalAlignment(JLabel.CENTER);
        status_panel.add(score1Label);
        final JLabel score2Label = new JLabel("0");
        score2Label.setHorizontalAlignment(JLabel.CENTER);
        status_panel.add(score2Label);
        final JLabel score3Label = new JLabel("0");
        score3Label.setHorizontalAlignment(JLabel.CENTER);
        status_panel.add(score3Label);

        // Main playing area
        final GameCourt court;
        try {
            name = JOptionPane.showInputDialog(null, "Username");
            court = new GameCourt(livesLabel, score1Label, score2Label, score3Label, name);
        }
        catch (IOException i) {
            throw new IllegalArgumentException("IO");
        }
        catch (javax.sound.sampled.LineUnavailableException i) {
            throw new IllegalArgumentException("Line Unavailable");
        }
        catch (javax.sound.sampled.UnsupportedAudioFileException i) {
            throw new IllegalArgumentException("Unsupported Audio File");
        }
        frame.add(court, BorderLayout.CENTER);

        // Buttons
        GridLayout control_grid = new GridLayout(2,3);
        final JPanel control_panel = new JPanel();
        control_panel.setLayout(control_grid);
        frame.add(control_panel, BorderLayout.NORTH);

        final JButton instruction = new JButton("Instructions");
        instruction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                court.pause();
                String message =
                        "Use the keyboard arrow keys to move your paddle and hit the ball,\n" +
                                "so that it hits the colored bricks. Bricks may require anywhere from\n" +
                                "1 to 4 hits to break, and some may be bombs that blow up other\n" +
                                "bricks! Grey bricks are unbreakable. Win the game by breaking\n" +
                                "all breakable bricks. After a loss or win, restart the game by\n" +
                                "pressing Start or Reset. If you want to change your username, use\n" +
                                "Reset. If you press Reset but don't put a username, you'll be called\n" +
                                "\"null\"! At any time, you can pause, reset, or save your game, or\n" +
                                "or load the most previously saved game, even from past plays! Your\n" +
                                "score is determined by how many bricks you hit and broke, and how long\n" +
                                "it took you to do so. You get 3 lives to make some magic, and your\n" +
                                "top scores will be held as long as the game is running. Good luck\n" +
                                "& have fun!";
                JOptionPane.showMessageDialog(null, message,
                        "Breakout Game Instructions", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        control_panel.add(instruction);

        // Note here that when we add an action listener to the reset button, we define it as an
        // anonymous inner class that is an instance of ActionListener with its actionPerformed()
        // method overridden. When the button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                court.pause();
                name = JOptionPane.showInputDialog(null, "Username");
                court.reset();
            }
        });
        control_panel.add(reset);
		
		final JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                court.start();
			}
		});
		control_panel.add(start);
		
		final JButton pause = new JButton("Pause");
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                court.pause();
            }
        });
        control_panel.add(pause);

        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
                try {
                    court.save();
                }
                catch (IOException i) {
                    throw new IllegalArgumentException();
                }
            }
        });
        control_panel.add(save);

        final JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
                try {
                    court.load();
                }
                catch (IOException i) {
                    throw new IllegalArgumentException();
                }
            }
        });
        control_panel.add(load);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        court.reset();
    }

    /**
     * Main method run to start and run the game. Initializes the GUI elements specified in Game and
     * runs it. IMPORTANT: Do NOT delete! You MUST include this in your final submission.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}