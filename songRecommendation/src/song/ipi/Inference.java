package song.ipi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import org.jblas.DoubleMatrix;

import song.database.DatabaseQuery;
import song.matrix.Graph;
/**
 * This class uses a difficulty ordering graph to compute 
 * recommendation probabilities.
 * @author lijiax
 * @version 1.0
 */
public class Inference {
	private HashMap<Integer, Integer> map;
	/**
	 * constructor function
	 * @param hash
	 */
	public Inference(HashMap<Integer, Integer> hash){
		map = hash;
	}
	/**
	 * get bonus vector for specific user
	 * @param userID user ID
	 * @param size the size of bonus vector
	 * @return a bonus vector
	 */
	private DoubleMatrix getBonusVector(int userID, int size){
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		DoubleMatrix bonus = DoubleMatrix.zeros(size);
		String sql = "select * from SongRecommendation_0rz9.UserSongTrainData where userID = userID";
		ResultSet result = DatabaseQuery.query(sql);
		try {
			while(result.next()){
				int songID = result.getInt(1);
				int index  =  map.get(songID);
				bonus.put(index, 1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		DoubleMatrix diff = m1.sub(m2);
		double sum = 0.0;
		for(int i=0; i < diff.length; i++)
			sum += Math.pow(diff.get(i), 2);
		return Math.sqrt(sum);
	}
	/**
	 * create performance degree vector for specific user
	 * using IPI algorithm
	 * @param userID user ID
	 * @return a performance degree vector
	 */
	public DoubleMatrix IPI(int userID){
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		Graph m = new Graph();
		HashMap<Integer, Integer> map = m.buildSongHashMap();
		int size = m.getSize();
		System.out.println("matrix size is " + size);
		double [] array = new double[size];
		double initialV = 1.0/size;
		for(int i = 0; i < size; i++)
			array[i] = initialV;
		double error = 1000, threshold = 0.0001, alpha = 0.3;
		DoubleMatrix graph= m.buildGraph(map, size);
		DoubleMatrix degree = new DoubleMatrix(array),
				     bonus  = getBonusVector(userID, size).muli(1-alpha),
				     next;
		
		DoubleMatrix transposedGraph = graph.transpose().muli(alpha);
		while(error > threshold){
			next = transposedGraph.mul(degree).add(bonus);
			error = getDifference(next, graph);
			degree = next;
		}
		return degree;
	}
	/**
	 * rank performance degree vector and select top k elements from it
	 * @param set performance degree vector
	 * @param k the number of elements to be recommended
	 * @return a group of song IDs
	 */
	public Vector<Integer> rankSongs(DoubleMatrix set, int k){
		Vector<Integer> songs = new Vector<Integer>(k);
		set.sortColumnsi();
		//select part songs from set and put them into vector
		for(int i = 0; i < k; i++){
			//value to key
		}
		return songs;
	}
}
