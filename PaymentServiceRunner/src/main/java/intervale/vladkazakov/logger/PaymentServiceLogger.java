package intervale.vladkazakov.logger;

import org.apache.log4j.Logger;

public class PaymentServiceLogger {

	private static Logger logger = Logger.getLogger(PaymentServiceLogger.class);
    private PaymentServiceLogger() {
    }

	// логирование сообщения
	public static void printMessage(String message) {
		logger.info(message);
    }

	public static void erroreMessage(String message) {
		logger.error(message);
	}

}
