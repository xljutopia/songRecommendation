package song.ipi;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.Vector;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import com.google.common.collect.BiMap;

import song.database.DatabaseQuery;
import song.matrix.Graph;

/**
 * This class uses a difficulty ordering graph to compute 
 * recommendation probabilities.
 * @author lijiax
 * @version 2.0
 */
public class Inference {
	//private HashMap<Integer, Integer> map;
	/**
	 * constructor function
	 */
	public Inference(){

	}
	/**
	 * get bonus vector for specific user
	 * @param map the song-index HashMap
	 * @param result the query result from selecting songs in train data
	 * @param size the size of bonus vector
	 * @return a bonus vector
	 */
	private DoubleMatrix getBonusVector(BiMap<Integer, Integer> map, ResultSet result, int size){
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		DoubleMatrix bonus = DoubleMatrix.zeros(size);
		int count = 0;
		try {			
			while(result.next()){
				int songID = result.getInt("songID");
				int index  =  map.get(songID);
				bonus.put(index, 1.0);
				count++;
			}		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Songs in history is "+count);
		return bonus;
	}
	/**
	 * 
	 * @param bonus
	 * @param map
	 * @param inputUserID
	 * @return
	 */
	private DoubleMatrix getPMEBonusVector(DoubleMatrix bonus, BiMap<Integer, Integer> map, int inputUserID){
		DoubleMatrix PMEBonus = bonus.mul(1.0);
		
		double averageDis = 0.0, threshold = 0.5;
		int size = map.size();
		int [] dis = new int[size];
		BiMap<Integer, Integer> reverseMap = map.inverse();
		//calculate distance between user and each song
		for(int i = 0; i < size; i++){
			dis[i] = Math.abs(inputUserID/100 - reverseMap.get(i));
			averageDis += dis[i]*1.0/size;
		}
		System.out.println("The average distance for "+inputUserID+" is "+averageDis);
		//close songs have high priority
		int count = 0;
		for(int i = 0; i < size; i++){
			if(dis[i] < averageDis*threshold){
				PMEBonus.put(i, 1);
				count++;
			}
		}
		System.out.println("PME qualified songs are "+count);
	
		return PMEBonus;
	}
	/**
	 * compute the squared difference between two vectors
	 * @param m1 vector1
	 * @param m2 vector2
	 * @return the squared difference
	 */
	private double getDifference(DoubleMatrix m1, DoubleMatrix m2){
		DoubleMatrix diff = m1.subColumnVector(m2);
		MatrixFunctions.powi(diff,2);
		return Math.sqrt(diff.sum());
	}
	/**
	 * use IPI to recommend songs for specific user
	 * using IPI algorithm
	 * @param userID user ID
	 * @param k the number of songs to be recommended
	 * @return an array of recommended song IDs
	 * @throws SQLException 
	 */
	public int[] IPI(int userID, int k, int type) {
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		
		Graph tool = new Graph();
		BiMap<Integer, Integer> map = tool.buildSongHashMap();
		int size = tool.getSize();
		System.out.println("The number of songs in database is " + size);
		double [] array = new double[size];
		double initialV = 1.0/size;
		for(int i = 0; i < size; i++)
			array[i] = initialV;
		double error = 1000, threshold = 0.0001, alpha = 0.3, beta = 1.0 - alpha;
		DoubleMatrix graph= tool.buildGraph(map, size, type);
		String sql = "select * from SongRecommendation_0rz9.UserSongTrainData where userID = "+userID;
		ResultSet result = DatabaseQuery.query(sql);		
		boolean isNewUser = false;
		try {
			if(!result.next()){
				System.out.println("Nonexistent UserID!");		
				isNewUser = true;		
			}
			else
				result.beforeFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DoubleMatrix degree = new DoubleMatrix(array), next, finalDegree = degree,
					 transposedGraph = graph.transpose();	
		if(!isNewUser){
			DoubleMatrix bonus  = getBonusVector(map, result, size);
			DoubleMatrix PMEbonus = getPMEBonusVector(bonus, map, userID).muli(beta);
			transposedGraph.muli(alpha);
			while(error > threshold){
				next = transposedGraph.mmul(degree);			
				next.addiColumnVector(PMEbonus);
				error = getDifference(next, degree);
				finalDegree = degree;
				degree = next;
			}
			//filter songs sung before in performance degree
			for(int i = 0; i < size; i++){
				if(0 == Double.compare(bonus.get(i), 1.0))	
					finalDegree.put(i, 0.0);
			}
		}
		else{
			while(error > threshold){
				next = transposedGraph.mmul(degree);			
				error = getDifference(next, degree);
				finalDegree = degree;
				degree = next;
			}
		}
				
		int recommendedSongIDs[] = new int[k];
		//converse index into song ID
		int i = 0;
		double max = 0.0;
		BiMap<Integer, Integer> reverseMap = map.inverse();
		while(i < k){
			max = finalDegree.max();
			int j = 0;
			while(j < size){
				if(i < k && 0 == Double.compare(finalDegree.get(j), max)){
					recommendedSongIDs[i++] = reverseMap.get(j);
					finalDegree.put(j, 0.0);
				}
				j++;
			}
		}
		return recommendedSongIDs;
	}
}