package song.main;

//import java.util.Scanner;


import song.database.DatabaseQuery;
import song.ipi.Inference;
import song.test.Test;
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
		
		//Scanner input = new Scanner(System.in);
		//int userID = input.nextInt();
		//int k = input.nextInt();
		int userID = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		int type = Integer.parseInt(args[2]);
		Inference ipi = new Inference();
		int[] vec = ipi.IPI(userID, k, type);

		//for(int v:vec){
		//	System.out.println(v);
		//}
		//System.out.println();
		int matchedSongs = Test.compareWithTestData(vec, userID);
		int songsInTestData = Test.getSize();
		if(songsInTestData == 0)
			System.out.println(0.0+","+0.0);
		else{
			double	precision = 100.0 * matchedSongs / k;
			double	recall = 100.0 * matchedSongs / songsInTestData;
				
			//System.out.println(precision+","+recall);
			System.out.printf("%.3f,%.3f\n", precision, recall);
		}
		//System.out.println("# of matched songs for "+userID+" is "+match);
		DatabaseQuery.close();
	}
}
