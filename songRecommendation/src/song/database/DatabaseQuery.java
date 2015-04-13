package song.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * This class includes methods to start a connection with MySQL,
 * use SQL statements to query database, and close a connection.
 * @author lijiax
 * @version 1.0
 */
public class DatabaseQuery {
	static final String driver = "com.mysql.jdbc.Driver";
	static final String url = "jdbc:mysql://130.127.201.224:3306/SongRecommendation_0rz9";
	static final String user = "SngRcmndtn_py6t";
	static final String password = "155959clemson";
	
	public static Connection connection = null;
	static Statement statement = null;
	static ResultSet resultSet = null;
	/**
	 * start a database connection
	 */
	public static void connect(){
		try{
			Class.forName(driver);			
		}
		catch(ClassNotFoundException e){
			System.out.println("can't find driver");
		}
		try{
			connection = DriverManager.getConnection(url, user, password);

			if(connection.isClosed())
				System.out.println("fail connecting to the Database!");
			
		}
		catch(SQLException e){		
			System.out.println("connection error!");
		}
	}
	/**
	 * use SQL language to query database
	 * @param sql SQL statement
	 * @return SQL execution result
	 */
	public static ResultSet query(String sql){		
		try{
			statement = connection.createStatement();
		    resultSet = statement.executeQuery(sql);		    
		}
		catch(SQLException e){
			System.out.println(sql+"  query error");
		}		
		return resultSet;
	}
	
	/**
	 * close a database connection
	 */
	public static void close() {
	    try {
	      if (resultSet != null) {
	        resultSet.close();
	      }

	      if (statement != null) {
	        statement.close();
	      }

	      if (connection != null) {
	        connection.close();
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	  }
}
