package song.matrix;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jblas.DoubleMatrix;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import song.database.DatabaseQuery;
/**
 * This class is to build a directed, weighted graph. Matrix is
 * a good way to implement it. Fill elements based on difficulty
 * orderings.
 * @author lijiax
 * @version 2.0
 */
public class Graph {
    private int size;
    /**
     * constructor function
     */
	public Graph(){
		size = 0;
	}
	/**
	 * get the matrix size
	 * @return the dimension of matrix
	 */
	public int getSize(){
		return size;
	}
	/**
	 * build a bidirectional HashMap whose key is song ID and value is index in matrix
	 * for songs in database
	 * @return a bidirectional HashBiMap
	 */
	public BiMap<Integer, Integer> buildSongHashMap(){
		BiMap<Integer, Integer> map = HashBiMap.create(); 
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		String sql = "select * from SongRecommendation_0rz9.Songs";
		ResultSet result = DatabaseQuery.query(sql);
		try {
			int i = 0;
			while(result.next()){
				//add some filter methods here
				map.put(result.getInt("songID"), i);
				i++;
			}
			size = i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * build a directed, weighted graph based on difficulty orderings
	 * @param map hashmap
	 * @param size matrix dimension
	 * @return
	 */
	public DoubleMatrix buildGraph(BiMap<Integer, Integer> map, int size){
		DoubleMatrix graph = DoubleMatrix.zeros(size, size);
		if(DatabaseQuery.connection == null)
			DatabaseQuery.connect();
		
		String sql = "select * from SongRecommendation_0rz9.DifficultyOrdering";
		ResultSet result = DatabaseQuery.query(sql);
		try{
			int source, destination;
			double support, confidence, weight;
			while(result.next()){
				source = result.getInt("sourceSongID");
				destination = result.getInt("destinationSongID");
				support = result.getDouble("support");
				confidence = result.getDouble("confidence");
				weight = support * confidence;
				
				int row = map.get(source);
				int column = map.get(destination);
				graph.put(row, column, weight);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return graph;
	}		
}
