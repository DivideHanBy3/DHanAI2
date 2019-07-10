import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.io.IOException;
import java.io.File;

/**
 * A robot which can be constructed and commanded.
 * Has the ability to turn left, turn right, and move one step forward.
 * Can see and share the tiles immediately adjacent
 * @author Spencer Yoder
 */
public class Robot extends Tile {
  /** The index assigned to up/north/-y direction */
  public static final int UP = 0;
  /** The index assigned to right/east/+x direction */
  public static final int RIGHT = 1;
  /** The index assigned to down/south/+y direction */
  public static final int DOWN = 2;
  /** The index assigned to left/west/-x direction */
  public static final int LEFT = 3;
  
  /** The file path to the sprite displayed when facing up */
  private static final String UP_PATH = "img/robotUp.png";
  /** The file path to the sprite displayed when facing down */
  private static final String DOWN_PATH = "img/robotDown.png";
  /** The file path to the sprite displayed when facing left */
  private static final String LEFT_PATH = "img/robotLeft.png";
  /** The file path to the sprite displayed when facing right */
  private static final String RIGHT_PATH = "img/robotRight.png";
  /** The default amount of time (in milliseconds) the robot waits between actions */
  private static final int CHILL_TIME = 100;
  /** The changes in y/x corresponding to each direction index */
  private static final int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
  
  /** An array containing the sprites for facing each direction */
  private BufferedImage[] faces;
  /** The room in which this robot lives */
  private Room room;
  /** The x position (in tiles) */
  private int x;
  /** The y position (in tiles) */
  private int y;
  /** The number of milliseconds between each action */
  private int chillTime;
  /** The direction the robot is currently facing */
  private int direction;
  /** The score this robot has. It's incremented by reaching goals and decremented by colliding 
      with hazards */
  private int score;
  
  /**
   * Constructs a new robot
   */
  public Robot() {
    super();
    faces = new BufferedImage[4];
    try {
      faces[0] = ImageIO.read(new File(UP_PATH));
      setImage(faces[0]);
      faces[1] = ImageIO.read(new File(RIGHT_PATH));
      faces[2] = ImageIO.read(new File(DOWN_PATH));
      faces[3] = ImageIO.read(new File(LEFT_PATH));
    } catch(IOException e) {
      throw new IllegalArgumentException("Could not create robot, could not access sprites");
    }
    direction = 0;
    chillTime = CHILL_TIME;
  }
  
  /**
   * Set the room to the given room
   * @param room the given room
   */
  public void setRoom(Room room) {
    this.room = room;
  }
  
  /**
   * Set the x coordinate to the given value
   * @param x the new x coordinate (in tiles)
   */
  public void setX(int x) {
    this.x = x;
  }
  
  /**
   * Set the y coordinate to the given value
   * @param y the new y coordinate (in tiles)
   */
  public void setY(int y) {
    this.y = y;
  }
  
  /**
   * @return the x coordinate (in tiles)
   */
  public int getX() {
    return x;
  }
  
  /**
   * @return the y coordinate (in tiles)
   */
  public int getY() {
    return y;
  }
  
  /**
   * Instructs the robot to turn left (relative to its current orientation)
   */
  public void turnLeft() {
    direction += 3;
    direction %= 4;
    setImage(faces[direction]);
    chillFor(chillTime);
  }
  
  /**
   * Instructs the robot to turn right (relative to its current orientation)
   */
  public void turnRight() {
    direction += 1;
    direction %= 4;
    setImage(faces[direction]);
    chillFor(chillTime);
  }
  
  /**
   * Instructs the robot to step forward in the direction it's currently facing
   */
  public void step() {
    Tile[] adjacent = room.getAdjacent(this);
    if(!(adjacent[direction] instanceof Wall)) {
      room.update(this, directions[direction][0], directions[direction][1]);
    }
    chillFor(chillTime);
  }
  
  /**
   * @return the direction this robot is currently facing
   */
  public int heading() {
    return direction;
  }
  
  /**
   * @return the tile adjacent to the robot in the given direction (absolute)
   * @param direction the given direction (Robot.UP, Robot.RIGHT, etc.)
   */
  public Tile tileAt(int direction) {
    return room.getAdjacent(this)[direction];
  }
  
  /**
   * @return the score
   */
  public int getScore() {
    return score;
  }
  
  /**
   * Add the given value to the score
   * @param ds the change in score
   */
  public void addToScore(int ds) {
    score += ds;
  }
  
  /**
   * Sets the time between actions to the given value
   * @param chillTime the number of milliseconds between actions
   */
  public void setChillTime(int chillTime) {
    if(chillTime < 0)
      chillTime = 0;
    this.chillTime = chillTime;
  }
  
  /**
   * Wait for the given number of milliseconds before executing the next action
   * @param millis the given number of milliseconds
   */
  public void chillFor(int millis) {
    try {
      Thread.sleep(millis);
    } catch(InterruptedException e) {
      //Do nothing
    }
  }
}
