package intervale.vladkazakov.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import intervale.vladkazakov.db.DBOperation;
import intervale.vladkazakov.db.DBOperationFactory;
import intervale.vladkazakov.db.ErrorMessage;
import intervale.vladkazakov.logger.PaymentServiceLogger;
import intervale.vladkazakov.models.Message;
import intervale.vladkazakov.models.Operation;

public class PaymentServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Message result = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
		String json = br.readLine();
		PaymentServiceLogger.printMessage(json);
		Gson gson = new Gson();
		Operation operation = gson.fromJson(json, Operation.class);
		System.out.println("Пришло на сервлет: " + operation);
		DBOperation dbOperation = DBOperationFactory.getDBOperation();
		try {
			result = dbOperation.checkOperation(operation);
			if (result == null) {
				String offert = dbOperation.doOperation(operation);
				if (offert != null) {
					resp.setContentType("application/json; charset=UTF-8");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().write(gson.toJson(new Message(offert)));
					System.out.println("ок");
				} else {
					resp.setContentType("application/json; charset=UTF-8");
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().write(gson.toJson(ErrorMessage.OPERATION_ERROR.getMessage()));
					System.out.println("bad");
				}
			} else {
				System.out.println("not ок");
				resp.setContentType("application/json; charset=UTF-8");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(gson.toJson(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
