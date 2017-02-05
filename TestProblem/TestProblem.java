import java.util.Scanner;
public class TestProblem
{
	public static void main (String[] args)
	{
		Scanner sc = new Scanner(System.in);
		
		int A = sc.nextInt();
		int B = sc.nextInt();
		
		int sum = A + B;
		int difference = A - B;
		int product = A * B;
		int quotient = A / B;
		int remainder = A % B;

		System.out.println( sum );
		System.out.println( difference );
		System.out.println( product );
		System.out.println( quotient );
		System.out.println( remainder );
	}
}
