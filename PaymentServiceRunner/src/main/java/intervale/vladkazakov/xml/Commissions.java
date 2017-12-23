package intervale.vladkazakov.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "commissions")
class Commissions {
	private List<Commission> commissions = null;

	@XmlElement(name = "commission")
	public void setCommissions(List<Commission> commissions) {
		this.commissions = commissions;
	}

	public List<Commission> getCommissions() {
		return commissions;
	}
}
