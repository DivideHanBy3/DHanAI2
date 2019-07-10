public class ExerciseFive{ //Start of program
	public static void main(String[] args) { //start main method
		int x = 9;
		int y = 7;
		int cars = x / y;
		int leftover = x % y;
		System.out.println("There are/is " + cars + " car(s) filled when there are " + x + " people and each car can hold " + y + " people");
		System.out.println("There are " + leftover + " people left when there are " + x + " people and each car can hold " + y + " people");
	}
}