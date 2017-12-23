package intervale.vladkazakov.runner;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import intervale.vladkazakov.db.ConnectionPool;
import intervale.vladkazakov.servlets.PaymentServlet;

public class Runner {
	private static Server server;
	private static final int MAX_THREADS = 100;
	private static final int MIN_THREADS = 10;
	private static final int TIMEOUT = 120;
	private static final int PORT=8080;

	public static final void main(String[] args) {
		/*
		 * потестить базу
		 */

		/*
		 * DBOperation dbOperation = DBOperationFactory.getDBOperation(); String
		 * result = ""; Operation operation = new Operation("HT1214UL23LK7890",
		 * "AV1214UL23LKTYK3", "023", "02/17", "VITALIY GORDEEV", new
		 * BigDecimal(100), "USD"); try { result =
		 * dbOperation.checkOperation(operation);
		 * dbOperation.doOperation(operation); } catch (SQLException e) {
		 * e.printStackTrace(); } System.out.println(result);
		 */

		/* потестить xml */

		QueuedThreadPool threadPool = new QueuedThreadPool(MAX_THREADS, MIN_THREADS, TIMEOUT);
		server = new Server(threadPool);
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(PORT);
		server.setConnectors(new Connector[] { connector });
		ServletContextHandler servletHandler = new ServletContextHandler();
		servletHandler.addServlet(PaymentServlet.class, "/operation");
		ServletHolder holderPwd = new ServletHolder("default", new DefaultServlet());
		holderPwd.setInitParameter("resourceBase", "./src/main/resources");
		servletHandler.addServlet(holderPwd, "/");
		server.setHandler(servletHandler);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}

	private static void finish() {
		ConnectionPool.close();
	}

}

// косяки: комментарии, appercase, тестить, логировать