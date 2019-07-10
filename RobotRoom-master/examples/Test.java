public class Test {
  public static void main(String[] args) {
    Room room = new Room("rooms/Test.txt");
    Robot robot = new Robot();
    robot.setChillTime(200);
    room.add(1, 1, robot);
    robot.turnRight();
    while(robot.getScore() == 0) {
      robot.step();
    }
    System.out.println(robot.getScore());
    robot.turnRight();
    robot.step();
    System.out.println(robot.getScore());
    robot.step();
  }
}
