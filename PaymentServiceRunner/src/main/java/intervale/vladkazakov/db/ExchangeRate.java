package intervale.vladkazakov.db;

import java.math.BigDecimal;

enum ExchangeRate {
	USD {
		public BigDecimal getCoefficient() {
			return new BigDecimal(1);
		}
	},
	BYN {
		public BigDecimal getCoefficient() {
			return new BigDecimal(2);
		}
	},
	RUB {
		public BigDecimal getCoefficient() {
			return new BigDecimal(55);
		}
	},
	EUR {
		public BigDecimal getCoefficient() {
			return new BigDecimal(0.8);
		}
	};
	public abstract BigDecimal getCoefficient(); 
}
