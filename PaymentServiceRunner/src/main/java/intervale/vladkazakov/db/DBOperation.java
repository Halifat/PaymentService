package intervale.vladkazakov.db;

import java.sql.SQLException;

import intervale.vladkazakov.models.Message;
import intervale.vladkazakov.models.Operation;

public interface DBOperation {
	String doOperation(Operation operation) throws SQLException;

	Message checkOperation(Operation operation) throws SQLException;
}
