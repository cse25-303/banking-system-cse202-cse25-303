package bank;

import java.math.BigDecimal;

/** A promise: any account that implements this can work out and pay interest. */
public interface InterestBearing {

    BigDecimal calculateInterest();

    void payInterest();
}
