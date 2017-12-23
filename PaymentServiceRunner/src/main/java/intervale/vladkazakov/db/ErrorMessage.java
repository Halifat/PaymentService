package intervale.vladkazakov.db;

import intervale.vladkazakov.models.Message;

public enum ErrorMessage {
	VALUE_ERROR {
		public Message getMessage() {
			return new Message("Недостаточно средств для операции");
		}
	},
	CLIENT_DATA_ERROR {
		public Message getMessage() {
			return new Message("Такого счета не существует");
		}
	},
	OPERATION_ERROR {
		public Message getMessage() {
			return new Message("Ошибка операции, попробуйте снова");
		}
	},
	CARD_DATA_ERROR {
		public Message getMessage() {
			return new Message("Неверные данные карты");
		}
	};
	public abstract Message getMessage();

}
