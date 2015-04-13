package song.ipi;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.Vector;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import com.google.common.collect.BiMap;

import song.database.DatabaseQuery;
import song.matrix.Graph;

//import com.google.guava:
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
	 * @param hash
	 */
	public Inference(){

	}
	/**
	 * get bonus vector for specific user
	 * @param map the song-index HashMap
	 * @param userID user ID
	 * @param size the size of bonus vector
	 * @return a bonus vector
	 */
	private DoubleMatrix getBonusVector(BiMap<Integer, Integer> map, int inputUserID, int size){
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		DoubleMatrix bonus = DoubleMatrix.zeros(size);
		String sql = "select * from SongRecommendation_0rz9.UserSongTrainData where userID = "+inputUserID;
		ResultSet result = DatabaseQuery.query(sql);
		try {
			if(!result.next())
				System.out.println("Nonexistent UserID!");
			else{
				while(result.next()){
					int songID = result.getInt(1);
					int index  =  map.get(songID);
					bonus.put(index, 1);
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bonus;
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
	 */
	public int[] IPI(int userID, int k){
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		Graph tool = new Graph();
		BiMap<Integer, Integer> map = tool.buildSongHashMap();
		int size = tool.getSize();
		System.out.println("matrix size is " + size);
		double [] array = new double[size];
		double initialV = 1.0/size;
		//System.out.println(initialV);
		for(int i = 0; i < size; i++)
			array[i] = initialV;
		double error = 1000, threshold = 0.0001, alpha = 0.2;
		DoubleMatrix graph= tool.buildGraph(map, size);
		DoubleMatrix degree = new DoubleMatrix(array),
				     bonus  = getBonusVector(map, userID, size).muli(1-alpha),
				     next ;
		
		DoubleMatrix transposedGraph = graph.transpose().muli(alpha);
		
		while(error > threshold){
			next = transposedGraph.mmul(degree);
			
			next.addiColumnVector(bonus);
			error = getDifference(next, degree);
			degree = next;
		}
		//filter songs sung before in performance degree
		for(int i = 0; i < size; i++){
			if(bonus.get(i) == (1-alpha))
				degree.put(i, 0.0);
		}
		
		int recommendedSongIDs[] = new int[k];
		//select part songs from set and put them into array
		int i = 0;
		double max = 0.0;
		BiMap<Integer, Integer> reverseMap = map.inverse();
		while(i < k){
			max = degree.max();
			int j = 0;
			while(j < size){
				if(i < k && max == degree.get(j)){
					recommendedSongIDs[i++] = reverseMap.get(j);
					degree.put(j, 0.0);
				}
				j++;
			}
		}
		return recommendedSongIDs;
	}
	
}
