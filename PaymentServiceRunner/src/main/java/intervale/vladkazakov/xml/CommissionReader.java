package intervale.vladkazakov.xml;

import java.io.File;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class CommissionReader {
	private static Commissions commissions;
	private static final String PATH_TO_DATA_FILE="commissions.xml";
	
	private CommissionReader() {
		
	}

	public static BigDecimal getCommissionValue(String brand, String currency) {
		if (commissions == null) {
			init();
		}
		for (Commission commission : commissions.getCommissions()) {
			if (commission.getBrand().equals(brand) && commission.getCurrency().equals(currency)) {
				return commission.getValue().setScale(2);
			}
		}
		return null;
	}

	private static void init() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Commissions.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			commissions = (Commissions) jaxbUnmarshaller.unmarshal(new File(PATH_TO_DATA_FILE));
			for (Commission commission : commissions.getCommissions()) {
				System.out.println(commission.getBrand() + ";" + commission.getCurrency() + ";" + commission.getValue());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
