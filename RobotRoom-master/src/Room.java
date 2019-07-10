import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

import java.io.FileNotFoundException;
import java.io.File;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.awt.Dimension;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * A class for a room which can hold tiles and a robot, opens a window when constructed.
 * Rooms can be laid out in text files, using W for walls, H for hazards, and G for goals.
 * A sample room looks like the following:
 * WWWWWWWWWWWWWWWW
 * W             GW
 * WH     WWWWWWWWW
 * W              W
 * WWWWWWWW      HW
 * W              W
 * W      WWWWWWWWW
 * WH             W
 * WWWWWWWWWWWWWWWW
 * @author Spencer Yoder
 */
public class Room {
  /** The number of milliseconds between frames in the displace */
  private static final int TICK_SPEED = 17;
  /** The wall which is placed outside every edge. The robot cannot got past edges of the screen */
  private static final Wall EDGE = new Wall();
  
  /** The grid of tiles */
  private Tile[][] grid;
  /** The display window */
  private JFrame frame;
  /** The canvas upon which things will be drawn */
  private Canvas c;
  /** True if the program is running, false if not */
  private boolean running;
  
  /**
   * Constructs a new, default room, 20 tiles wide and 20 tiles tall
   */
  public Room() {
    init("Default Room", Tile.WIDTH * 20, Tile.HEIGHT * 20);
  }
  
  /**
   * Constructs a new room with tiles taken from the text file with the given name
   * @param filename the path to the text file containing the layout for this room
   */
  public Room(String filename) {
    Scanner in;
    try {
      in = new Scanner(new File(filename));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Error: file " + filename + " could not be accessed.");
    }
    ArrayList<Tile> list = new ArrayList<Tile>();
    int w = 0;
    int h = 0;
    boolean wide = false;
    while(in.hasNextLine()) {
      String l = in.nextLine();
      h++;
      if(!wide) {
        w = l.length();
        wide = true;
      }
      for(int i = 0; i < w; i++) {
        if(i < l.length()) {
          char c = l.charAt(i);
          if(c == 'W') {
            list.add(new Wall());
          } else if(c == 'G') {
            list.add(new Goal());
          } else if(c == 'H') {
            list.add(new Hazard());
          } else {
            list.add(null);
          }
        } else {
          list.add(null);
        }
      }
    }
    if(w == 0) {
      throw new IllegalArgumentException("Error: file cannot be empty");
    }
    int idx = 0;
    grid = new Tile[w][h];
    for(int i = 0; i < h; i++) {
      for(int j = 0; j < w; j++) {
        grid[j][i] = list.get(idx++);
      }
    }
    init(filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")), w, h);
  }
  
  /**
   * Initializes the room with the given title, width, and height
   * @param s the title for the window
   * @param w the width, in pixels, of the window
   * @param h the height, in pixels, of the window
   */
  private void init(String s, int w, int h) {
    frame = new JFrame(s);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    if(grid == null)
      grid = new Tile[w][h];
    frame.setSize(Tile.WIDTH * w, Tile.HEIGHT * h);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    
    c = new Canvas();
    c.setPreferredSize(new Dimension(Tile.WIDTH * w, Tile.HEIGHT * h));
    frame.add(c);
    frame.pack();
    frame.setVisible(true);
    
    Thread t = new Thread(new GameLoop());
    running = true;
    t.start();
  }
  
  /**
   * Add the given tile at the given x and y coordinates
   * @param x the x coordinate, in tiles. Starts at 0 on the left and increases to the right
   * @param y the y coordinate in tiles. Starts at 0 at the top and increases going downwards
   * @param t the tile to be added
   */
  public synchronized void add(int x, int y, Tile t) {
    if(x < 0 || x >= grid.length || y < 0 || y >= grid[0].length)
      return;
    if(t == null || grid[x][y] == null) {
      grid[x][y] = t;
      if(t instanceof Robot) {
        Robot r = (Robot) t;
        r.setRoom(this);
        r.setX(x);
        r.setY(y);
      }
    }
  }
  
  /**
   * Returns the array of tiles in NESW order which are immediately adjacent to the robot
   * Empty tiles are represented as null.
   * @param r the robot around which the tiles are adjacent
   * @return the array of tiles in NESW order which are immediately adjacent to the robot
   */
  public synchronized Tile[] getAdjacent(Robot r) {
    int x = r.getX();
    int y = r.getY();
    return new Tile[] {adjacent(r.getX(), r.getY() - 1), adjacent(r.getX() + 1, r.getY()), 
                       adjacent(r.getX(), r.getY() + 1), adjacent(r.getX() - 1, r.getY())};
  }
  
  /**
   * Helper method for getAdjacent(). Checks boundaries for tiles
   * @param x the x position of the tile to check
   * @param y the y position of the tile to check
   * @return the tile at the given x and y coordinates. A wall tile if the x and y are outside of the grid.
   */
  private Tile adjacent(int x, int y) {
    if(x < 0 || x >= grid.length || y < 0 || y >= grid[0].length)
      return EDGE;
    return grid[x][y];
  }
  
  /**
   * Called by the robot to refresh the room once the robot moves
   * This method handles the proper calculation of scores for hazards/goals
   * @param r the robot which called the method
   * @param dx the change in x the robot wants to move
   * @param dy the change in y the robot wants to move
   */
  public synchronized void update(Robot r, int dx, int dy) {
    Tile t = grid[r.getX() + dx][r.getY() + dy];
    if(t == null || !t.isWall()) {
      grid[r.getX()][r.getY()] = null;
      if(t != null) {
        if(t.isHazard()) {
          r.addToScore(-1);
        } else if(t.isGoal()) {
          r.addToScore(1);
        }
      }
      grid[r.getX() + dx][r.getY() + dy] = r;
      r.setX(r.getX() + dx);
      r.setY(r.getY() + dy);
    }
  }
  
  /**
   * Called by the room every tick to update the canvas
   */
  public synchronized void render() {
    BufferStrategy bs = c.getBufferStrategy();
    if(bs == null)
      c.createBufferStrategy(3);
    else {
      Graphics g = bs.getDrawGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      for(int i = 0; i < grid.length; i++) {
        for(int j = 0; j < grid[i].length; j++) {
          if(grid[i][j] != null)
            grid[i][j].render(i * Tile.WIDTH, j * Tile.HEIGHT, g);
        }
      }
      bs.show();
      g.dispose();
    }
  }
  
  /**
   * Call this method to halt the program but not close the window
   */
  public synchronized void kill() {
    running = false;
  }
  
  /**
   * Holds the game loop which updates the screen every tick (17 ms intervals)
   * Runs the game loop on a seperate thread from the main program
   * @author Spencer Yoder
   */
  private class GameLoop implements Runnable {
    /**
     * Refreshes the screen every 17 milliseconds and draws every tile
     */
    @Override
    public void run() {
      long time = System.currentTimeMillis();
      while(running) {
        long current = System.currentTimeMillis();
        if(current - time >= TICK_SPEED) {
          time = current;
          render();
        }
      }
    }
  }
}