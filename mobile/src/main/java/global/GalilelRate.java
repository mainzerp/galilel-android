package global;

import java.math.BigDecimal;

public class GalilelRate {

    /** Coin letters (USD,EUR,etc..) */
    private final String code;
    /** Value of 1 GALI in this rate */
    private final BigDecimal rate;
    /** Last update time */
    private final long timestamp;

    public GalilelRate(String code, BigDecimal rate, long timestamp) {
        this.code = code;
        this.rate = rate;
        this.timestamp = timestamp;

    }

    public String getCode() {
        return code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Old method..
     */
    public String getLink(){
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
