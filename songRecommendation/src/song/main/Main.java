package song.main;

import java.util.HashMap;

import org.jblas.DoubleMatrix;

import song.database.DatabaseQuery;
import song.matrix.Graph;
/**
 * Main class
 * @author lijiax
 * @version 1.0
 */
public class Main {
	public static void main(String[] args ){
		DatabaseQuery.connect();
		Graph m = new Graph();
		HashMap<Integer, Integer> map = m.buildSongHashMap();
		int size = m.getSize();
		System.out.println("matrix size is " + size);
		DoubleMatrix graph= m.buildGraph(map, size);
		System.out.println("complete!");
		//graph = matrix.buildGraph(map, size);
		DatabaseQuery.close();
	}
}
