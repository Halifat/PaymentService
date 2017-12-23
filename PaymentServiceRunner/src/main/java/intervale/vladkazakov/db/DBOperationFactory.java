package intervale.vladkazakov.db;

public class DBOperationFactory {
	private DBOperationFactory() {

	}

	public static DBOperation getDBOperation() {
		return new DBOperationImpl();
	}

}
