public class CHALLENGE{ //Start of program
	public static void main(String[] args) { //start main method
		int x = 7635;
		int sum = (x/1000) + ((x/100)%10) + ((x/10)%10) + (x % 10);
		System.out.println(sum);
	}
}