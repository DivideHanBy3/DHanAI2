import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.io.IOException;
import java.io.File;

/**
 * An abstract class for a tile that can go on a grid
 * @author Spencer Yoder
 */
public abstract class Tile {
  /** The width in pixels of a tile */
  public static final int WIDTH = 40;
  /** The height in pixels of a tile */
  public static final int HEIGHT = 40;
  
  /** The sprite to be displayed for the tile */
  private BufferedImage image;
  
  /**
   * Constucts a default tile, used by the Robot class
   */
  public Tile() {}
  
  /**
   * Constructs a tile to use the image at the given path as a sprite
   * @param imgPath the path to the image for the sprite for this tile
   */
  public Tile(String imgPath) {
    try {
      setImage(ImageIO.read(new File(imgPath)));
    } catch(IOException e) {
      throw new IllegalArgumentException("Error. Resource with path " + imgPath + " could not be found.");
    }
  }
  
  /**
   * Draw this tile, with the top left corner at x and y, on the given graphics object
   * @param x the x coordinate of the top left corner
   * @param y the y coordinate of the top left corner
   * @param g the graphics object on which to draw the sprite
   */
  public void render(int x, int y, Graphics g) {
    g.drawImage(image, x, y, null);
  }
  
  /**
   * Set the sprite for this tile
   * @param image the image to set as the new sprite
   */
  protected void setImage(BufferedImage image) {
    this.image = image;
  }
  
  /**
   * @return true if this tile is a wall
   */
  public boolean isWall() {
    return this instanceof Wall;
  }
  
  /**
   * @return true if this tile is a hazard
   */
  public boolean isHazard() {
    return this instanceof Hazard;
  }
  
  /**
   * @return true if this tile is a goal
   */
  public boolean isGoal() {
    return this instanceof Goal;
  }
}
