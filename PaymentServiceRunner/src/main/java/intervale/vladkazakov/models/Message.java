package intervale.vladkazakov.models;

//класс для сообщения от сервера на запрос
public class Message {
	private String message;

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
