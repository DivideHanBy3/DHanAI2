import java.util.Random;
import java.util.ArrayList;

import java.awt.Point;

public class Snake {
  private static boolean winning;
  private static int goalX;
  private static int goalY;
  public static void main(String[] args) {
    Room room = new Room("rooms/Snake.txt");
    Robot robot = new Robot();
    room.add(10, 10, robot);
    goalX = -1;
    Random r = new Random();
    ArrayList<Point> tail = new ArrayList<Point>();
    winning = true;
    while(winning) {
      if(goalX == -1) {
        int rx = r.nextInt(18) + 1;
        int ry = r.nextInt(18) + 1;
        room.add(rx, ry, new Goal());
        goalX = rx;
        goalY = ry;
      }
      decideWhereToTurn(robot);
      int score = robot.getScore();
      int formerX = robot.getX();
      int formerY = robot.getY();
      robot.step();
      if(robot.getScore() < score) {
        room.kill();
        System.out.println("Tail length: " + score);
        winning = false;
      } else {
        room.add(formerX, formerY, new Hazard());
        tail.add(0, new Point(formerX, formerY));
        if(robot.getScore() == score) {
          Point p = tail.remove(tail.size() - 1);
          room.add(p.x, p.y, null);
        } else {
          goalX = -1;
        }
      }
    }
  }
  
  private static void decideWhereToTurn(Robot robot) {
    int direction = robot.heading();
    Tile front = robot.tileAt(direction);
    Tile left = robot.tileAt((direction + 3) % 4);
    Tile right = robot.tileAt((direction + 1) % 4);
    int preferred = towards(robot);
    if(left != null && left.isGoal())
      robot.turnLeft();
    else if(right != null && right.isGoal())
      robot.turnRight();
    else if(front != null && front.isHazard()) {
      if(right != null && right.isHazard() && left != null && left.isHazard()) {
        //Do nothing
      } else if(right != null && right.isHazard()) {
        robot.turnLeft();
      } else if(left != null && left.isHazard()){
        robot.turnRight();
      } else {
        if(preferred == -1 || preferred == 0 && Math.random() < 0.5)
          robot.turnLeft();
        else
          robot.turnRight();
      }
    } else {
      if(right != null && right.isHazard() && left != null && left.isHazard()) {
        //Do nothing
      } else if(right != null && right.isHazard()) {
        if(preferred == -1)
          robot.turnLeft();
      } else if(left != null && left.isHazard()){
        if(preferred == 1)
          robot.turnRight();
      } else {
        if(preferred == -1)
          robot.turnLeft();
        else if(preferred == 1)
          robot.turnRight();
      }
    }
  }
  
  private static int towards(Robot robot) {
    int theta = robot.heading();
    int x = theta == 1 ? 1 : theta == 3 ? -1 : 0;
    int y = theta == 2 ? 1 : theta == 0 ? -1 : 0;
    int dx = goalX - robot.getX();
    int dy = goalY - robot.getY();
    dx = dx > 0 ? 1 : dx < 0 ? -1 : 0;
    dy = dy > 0 ? 1 : dy < 0 ? -1 : 0;
    if(dx == x || dy == y)
      return 0;
    theta += 3;
    theta %= 4;
    x = theta == 1 ? 1 : theta == 3 ? -1 : 0;
    y = theta == 2 ? 1 : theta == 0 ? -1 : 0;
    if(dx == x || dy == y)
      return -1;
    theta += 2;
    theta %= 4;
    x = theta == 1 ? 1 : theta == 3 ? -1 : 0;
    y = theta == 2 ? 1 : theta == 0 ? -1 : 0;
    if(dx == x || dy == y)
      return 1;
    return Math.random() < 0.5 ? 0 : 1;
  }
}
