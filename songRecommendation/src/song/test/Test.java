package song.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import song.database.DatabaseQuery;
/**
 * Test how many recommended songs match test data in database
 * @author lijiax
 * @version 1.0
 */
public class Test {
	/**
	 * count number of matched songs in test data
	 * @param finalDegree
	 * @param userID
	 * @return
	 */
	public static int compareWithTestData(int[] finalDegree, int userID){
		int count = 0, size = 0;
		String sql = "select * from SongRecommendation_0rz9.UserSongTestData where userID = "+userID;
		ResultSet result = DatabaseQuery.query(sql);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			if(!result.next())
				System.out.println("Nonexistent UserID!");
			else{
				while(result.next()){
					int songID = result.getInt("songID");
					map.put(songID, size++);
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0; i < finalDegree.length; i++){
			if(map.containsKey(finalDegree[i])){
				count++;
				System.out.print(finalDegree[i]+" ");
			}
		}
		//System.out.println();
		return count;
	}
}
