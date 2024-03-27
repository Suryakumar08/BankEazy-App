package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exception.CustomBankException;

public class JDBCConnector {
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "Surya@131419@sS";
	
	private static Connection connection = null;
	
	private JDBCConnector() {
		
	}

	public static Connection getConnection(String dbName) throws CustomBankException{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL + dbName, USERNAME, PASSWORD);
		} catch (ClassNotFoundException | SQLException e) {
			throw new CustomBankException("Connection failed!", e);
		}
		return connection;
	}

}
