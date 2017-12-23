package intervale.vladkazakov.db;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import intervale.vladkazakov.models.Message;
import intervale.vladkazakov.models.Operation;
import intervale.vladkazakov.xml.CommissionReader;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

class DBOperationImpl implements DBOperation {
	private final static String FIND_ACCOUNT = "select* from Bank_accounts where number=? and code=? and date=? and name=?";//запрос для поиска клиента по заданным параметрам
	private final static String UPDATE_ACCOUNT = "update Bank_accounts set value=? where number=?";//обновление счетом клиентов
	private final static String FIND_CLIENT = "select value,brand,currency from Bank_accounts where number=?";//поиск клиента по номеру карты
	private final static String INSERT_PAYMENT = "insert into Payments (from_account,to_account,currency,commission,date,value) values(?,?,?,?,?,?)";
	private static Liquibase liquibase = null;

	DBOperationImpl() {
		
	}

	static {
		try {
			Class.forName("org.h2.Driver");//загрузка драйвера
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try (Connection conn = ConnectionPool.getConnection()) {
		Database database = null;
		try {
			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
		} catch (DatabaseException e1) {
			e1.printStackTrace();
		}

		try {
			liquibase = new Liquibase("liquibase/db-changelog-master.xml", new FileSystemResourceAccessor(), database);
				liquibase.update("");
		} catch (LiquibaseException e1) {
			e1.printStackTrace();
		}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public Message checkOperation(Operation operation) {
		ResultSet resultSet = null;
		try (Connection conn = ConnectionPool.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_ACCOUNT)) {
			stmt.setString(1, operation.getFromAccount());
			stmt.setString(2, operation.getCode());
			stmt.setString(3, operation.getDate());
			stmt.setString(4, operation.getName());
			resultSet = stmt.executeQuery();
			resultSet.next();
			BigDecimal valueClient = resultSet.getBigDecimal("value");
			String currency = resultSet.getString("currency");
			if (valueClient.compareTo(convert(operation.getValue(), operation.getCurrency(), currency)) == -1) {
				return ErrorMessage.VALUE_ERROR.getMessage();
			} else {
				if (checkClient(conn, operation)) {
					return ErrorMessage.CLIENT_DATA_ERROR.getMessage();
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ErrorMessage.CARD_DATA_ERROR.getMessage();
		}

		return null;
	}

	private boolean checkClient(Connection connection, Operation operation) throws SQLException {
		// ошибка с определением получателя
		ResultSet resultSet = null;
		PreparedStatement stmt = connection.prepareStatement(FIND_CLIENT);
		stmt.setString(1, operation.getToAccount());
		resultSet = stmt.executeQuery();
		if (resultSet.next()) {
			return false;
		} else {
			return true;
		}
	}

	// высчитать итоговую сумму(проверить валюту ввода(сравнить с
	// валютой карты(BYN); если необходимо - перевести; отнять комиссию
	// забрать у первого
	// отослать второму
	// проверить валюту второго, рсавнить с валютой перевода. Если
	// необходимо - привести к валюте карты(BYN);
	@Override
	public String doOperation(Operation operation) throws SQLException {
		String currency;
		ResultSet resultSet = null;
		BigDecimal value = null;
		Connection conn = null;
		try {
			conn = ConnectionPool.getConnection();
			try (PreparedStatement stmt = conn.prepareStatement(FIND_CLIENT)) {
				conn.setAutoCommit(false);
				stmt.setString(1, operation.getFromAccount());
				resultSet = stmt.executeQuery();
				resultSet.next();
				BigDecimal money = resultSet.getBigDecimal(1);
				String brand = resultSet.getString(2);
				currency = resultSet.getString(3);
				operation.setBrand(brand);
				value = new BigDecimal(operation.getValue().doubleValue());
				value = convert(value, operation.getCurrency(), currency);
				System.out.println("после конвертации валюты: " + value);
				value = calcCommission(value, operation);
				System.out.println("с вычитом комиссии: " + value);
				money = money.add(value.negate());
				System.out.println("счет отправителя в результате:" + money);
				updateAccount(conn, operation.getFromAccount(), money);// изменение
																		// отправителя
			}
			try (PreparedStatement stmt = conn.prepareStatement(FIND_CLIENT)) {
				stmt.setString(1, operation.getToAccount());
				resultSet = stmt.executeQuery();
				resultSet.next();
				BigDecimal money = resultSet.getBigDecimal(1);
				String currencyClient = resultSet.getString(3);
				System.out.println("счет получателя изначально:" + money);
				System.out.println("добавлено:" + value);
				money = money.add(convert(value, currency, currencyClient));
				System.out.println("счет получателя в результате:" + money);
				updateAccount(conn, operation.getToAccount(), money);
			}
			// запись платежа
			writePayment(conn, operation, value, currency);
			conn.commit();
		} catch (SQLException | ArithmeticException | NullPointerException e) {
			conn.rollback();
			e.printStackTrace();
			return null;
		} finally {
			conn.close();
		}
		// составление оферты
		return createOffert(operation);
	}

	private void updateAccount(Connection connection, String number, BigDecimal value) {
		try (PreparedStatement stmt = connection.prepareStatement(UPDATE_ACCOUNT)) {
			stmt.setBigDecimal(1, value.setScale(2, BigDecimal.ROUND_FLOOR));
			stmt.setString(2, number);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String createOffert(Operation operation) {

		StringBuilder result = new StringBuilder("Произведен перевод с системы ");
		result.append(operation.getBrand() + "." + "<br>");
		result.append("Со счета: " + hideNumber(operation.getFromAccount() + "<br>"));
		result.append("На счет: " + hideNumber(operation.getToAccount() + "<br>"));
		result.append("Валюта перевода: " + operation.getCurrency() + "<br>");
		result.append("На сумму: " + operation.getValue() + "<br>");
		result.append("В том числе комиссия: " + operation.getCommission().setScale(2, BigDecimal.ROUND_FLOOR) + "<br>");
		result.append("Итого: " + (operation.getValue().add(operation.getCommission().negate()).setScale(2, BigDecimal.ROUND_FLOOR) + "<br>"));
		return result.toString();
	}

	private String hideNumber(String number) {
		StringBuilder result = new StringBuilder(number);
		for(int i=0;i<5;i++) {
			result.setCharAt(i+2, '*');
		}
		return result.toString();
	}

	private void writePayment(Connection connection, Operation operation, BigDecimal resultValue, String fromCurrency) {
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_PAYMENT)) {
			System.out.println(resultValue);
			resultValue = convert(resultValue, fromCurrency, operation.getCurrency());
			System.out.println("fgwmg  " + resultValue);
			stmt.setString(1, operation.getFromAccount());
			stmt.setString(2, operation.getToAccount());
			stmt.setString(3, operation.getCurrency());
			BigDecimal commission=operation.getValue().add(resultValue.negate());
			System.out.println("комиссия в итоге: " + commission);
			operation.setCommission(commission);
			stmt.setBigDecimal(4, commission.setScale(2, BigDecimal.ROUND_FLOOR));
			stmt.setDate(5, new Date(System.currentTimeMillis()));
			stmt.setBigDecimal(6, resultValue.setScale(2, BigDecimal.ROUND_FLOOR));
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// метод конвертации валюты
	private BigDecimal convert(BigDecimal value, String from, String to) {
		System.out.println("convert from " + from + " to " + to);
		switch (from) {
		case "USD":
			value = value.multiply(new BigDecimal(1).divide(ExchangeRate.USD.getCoefficient()));
			break;
		case "RUB":
			value = value.multiply(new BigDecimal(1.0).divide(ExchangeRate.RUB.getCoefficient()));
			break;
		case "BYN":
			value = value.multiply(new BigDecimal(1.0).divide(ExchangeRate.BYN.getCoefficient()));
			break;
		case "EUR":
			value = value.multiply(new BigDecimal(1.0).divide(ExchangeRate.EUR.getCoefficient(), MathContext.DECIMAL128));
			break;
		default:
			break;
		}

		switch (to) {
		case "USD":
			value = value.multiply(ExchangeRate.USD.getCoefficient());
			break;
		case "RUB":
			value = value.multiply(ExchangeRate.RUB.getCoefficient());
			break;
		case "BYN":
			value = value.multiply(ExchangeRate.BYN.getCoefficient());
			break;
		case "EUR":
			value = value.multiply(ExchangeRate.EUR.getCoefficient());
			break;
		default:
			break;
		}
		return value;
	}

	private BigDecimal calcCommission(BigDecimal value, Operation operation) throws NullPointerException {
		// operation.setCommission(2.5);
		System.out.println("brand and currency operation: " + operation.getBrand() + ";" + operation.getCurrency());
		BigDecimal commission = CommissionReader.getCommissionValue(operation.getBrand(), operation.getCurrency());
		System.out.println(commission);
		BigDecimal coef = new BigDecimal(100);
		commission = coef.add(commission.negate()).divide(coef);// проверить
		value = value.multiply(commission);
		return value;
	}


}
