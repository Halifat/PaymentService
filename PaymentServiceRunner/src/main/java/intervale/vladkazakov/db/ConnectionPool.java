package intervale.vladkazakov.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

//пул соединений к базе
public class ConnectionPool {
	private final static String URL = "jdbc:h2:./PaymentServiceRunner";
	private static JdbcConnectionPool cp;

	private ConnectionPool() {
	}

	public static Connection getConnection() throws SQLException {
		if (cp == null) {
			cp = JdbcConnectionPool.create(URL, "", "");
		}
		return cp.getConnection();
	}

	public static void close() {
		System.out.println("yes");
		cp.dispose();
	}
}
