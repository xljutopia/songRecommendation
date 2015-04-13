package song.main;

import java.util.Scanner;

import song.database.DatabaseQuery;
import song.ipi.Inference;
/**
 * Main class
 * input user ID and # of songs to be recommended
 * output song IDs
 * @author lijiax
 * @version 2.0
 */
public class Main {
	public static void main(String[] args ){
		DatabaseQuery.connect();
		
		Scanner input=new Scanner(System.in);
		int userID = input.nextInt();
		int k = input.nextInt();
		Inference ipi = new Inference();
		int[] vec = ipi.IPI(userID, k);

		for(int v:vec){
			System.out.print(v+"  ");
		}
		DatabaseQuery.close();
	}
}
