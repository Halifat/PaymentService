package intervale.vladkazakov.xml;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "commission")
class Commission {
	private String brand;
	private String currency;
	private BigDecimal value;

	@XmlElement(name = "brand")
	public void setBrand(String brand) {
		this.brand = brand;
	}

	@XmlElement(name = "currency")
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@XmlElement(name = "value")
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getBrand() {
		return brand;
	}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getValue() {
		return value;
	}
}
