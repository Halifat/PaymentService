package intervale.vladkazakov.models;

import java.math.BigDecimal;

//класс, содержащий в себя описание операции по переводу средств
public class Operation {
	private String fromAccount;// номер карты плательщика
	private String toAccount;// номер карты получателя
	private String code;
	private String date;
	private String name;
	private BigDecimal value;
	private String currency;
	private String brand;
	private BigDecimal commission;

	public Operation(String fromAccount, String toAccount, String code, String date, String name, BigDecimal value, String currency) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.code = code;
		this.date = date;
		this.name = name;
		this.value = value;
		this.currency = currency;
	}

	public String getCode() {
		return code;
	}

	public String getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public String getFromAccount() {
		return fromAccount;
	}



	public String getToAccount() {
		return toAccount;
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getCurrency() {
		return currency;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	@Override
	public String toString() {
		return (fromAccount + ";" + toAccount + ";" + brand + ";" + code + ";" + currency + ";" + date + ";" + name + ";" + value + ";" + commission + ";");
	}

}
