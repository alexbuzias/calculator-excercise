package digital.metro.pricing.calculator.dto;

import java.math.BigDecimal;
import java.util.Map;

public class BasketCalculationResult {

    private final String customerId;
    private final Map<String, BigDecimal> pricedBasketEntries;
    private final BigDecimal totalAmount;

    public BasketCalculationResult(String customerId, Map<String, BigDecimal> pricedBasketEntries, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.pricedBasketEntries = pricedBasketEntries;
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Map<String, BigDecimal> getPricedBasketEntries() {
        return pricedBasketEntries;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
