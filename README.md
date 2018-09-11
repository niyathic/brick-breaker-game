# brick-breaker-game

  Ball.java extends the provided GameObj class, and is an object representing the ball that bounces off
  the paddle and hits the bricks. The ball has size, position, velocity and color.

  BrickType.java is a simple enum for the 3 brick types: EMPTY, REGULAR, BOMB and UNBREAKABLE. These are
  used in Brick.getBrickType(), which is used in GameCourt to check Brick types when iterating through
  the brickArray.

  Brick.java is the abstract Brick class, extended by its Brick subtypes. See the above note on subtyping.
  Direction.java is a provided enum for 4 directions: UP, DOWN, LEFT and RIGHT, used in GameObj.java.

  Game.java is a provided class which specifies the frame and widgets of the GUI. I edited this class to
  change the labels to contain lives and scores, add buttons for start(), pause(), save() and load() on top
  of the original reset(), add JOptionPane.showInputDialog to get user input for the username, throw
  necessary exceptions, and pass in the labels and username into the GameCourt constructor.

  GameCourt.java holds the primary game logic for how different objects interact with one another. It stores
  the game state information, including lives, username, objects like paddle and ball, brickArray,
  topScores and booleans about gameplay, a Stopwatch to calculate score, and music/sound effects to play.
  It handles arrow key presses by moving the paddle (changing its velocity), and has functions to calculate
  the score at the end of the game, display top scores in their labels, make bricks (used to generate
  the brickArray in reset() and load()), and handle the buttons given in Game. It repaints the GUI on
  every tick(), where it checks for ball-paddle, ball-South border and ball-brick collisions, handles brick
  hits, updates variables, and checks for game-ending conditions.

  GameObj.java is a provided class for objects in the game. I did not alter this file.

  InfoReader.java is a class to read the saved.txt file written to in the GameCourt's save() function.
  InfoReader is called in GameCourt.load(). InfoReader makes a new BufferedReader and reads the file
  (which is one long line), separates it into a String array by its commas, and puts the parsed integer
  values into an int array, which it gives in a getter. This is then read by load() to update the game
  state.

  Paddle.java, like Ball.java, extends GameObj.java, and has size, position, velocity and color. It
  represents the paddle which moves to hit the ball.

  Stopwatch.java is a class that I *took from an outside source* (with permission from Dr. Weirich)
  for a stopwatch to use to calculate elapsed time, which I use in determining a user's score. This
  class is better than simply using System.getCurrentTimeMillis() because it easily accounts for pausing
  the game multiple times.

  UserScore.java is a simple class for users containing their name and score for a specific finished
  game. Its constructor simply defines these fields, and it has getters for these fields. It implements
  Comparable so that Collections.sort() can sort it based on scores. To do this, I overrode the
  compareTo() method.


  Stopwatch.java is taken from: https://gist.github.com/EdHurtig/78cbe307c1c85db12af7
  Skeleton code for Game and GameCourt, and code for Ball, taken from UPenn CIS 120 Hw.
  
  Concepts/data types

  I implement a 2D array of Bricks (belonging to the Brick class). It is a 13 x 8 array of Bricks which
  models the internal state of the game; it is iterated through to check whether the ball has hit Brick(s),
  update Bricks accordingly and check for the game-winning condition of having all empty Bricks. The array
  is an efficient way to store the Brick objects and thus the state of the game, as it allows iteration
  to check and perform these actions, and directly represents the Brick grid seen in game play.

  I have a Brick abstract class, with several subtypes, BombBrick, RegularBrick and UnbreakableBrick,
  that extend the class. This allows for common Brick construction and methods like making Bricks,
  getting code, type, hits and whether the brick has been hit, changing hits, drawing Bricks and making
  Bricks empty. Subtypes have their own codes and number of "available" hits, and allow specific colors,
  and for overriding the RegisterHit() method by UnbreakableBrick to ensure it never registers a hit.
  Bricks can thus have their own ways of handling hits.

  I have a List, implemented as a LinkedList, of UserScores (a class I made with username and score from
  that game). At any point of time the List will have 3 UserScores, except for before one game is played,
  when the List is empty. A List is an efficient way of storing this information as it allows duplicates
  and sorting, can add elements, and can remove elements so that no more than 3 elements are present at
  one time. This List could easily be expanded by changing the NUM_SCORES constant, and more elements can
  be displayed by adding labels to the GridLayout. It also allows easy for-each iteration, seen in save().

  I use file I/O to write and read information in the save() and load() buttons. save() writes an integer
  CSV using a BufferedWriter to saved.txt, found in files. I added String / int encoding and decoding
  functions to let save() write and load() read usernames in topScores. save() saves key information about
  the game state, including the elapsed time (converted to an int), lives, ball and paddle positions,
  topScores and the brickArray, and load() sets these variables to the saved values, as well as checks
  whether the saved game had already been lost or won. File I/O provides a quick and efficient way to
  save and load this data and update the game state to a past one accordingly. load() can even be used to
  load a past game after quitting the program and re-running it.
