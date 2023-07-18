package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.dto.Basket;
import digital.metro.pricing.calculator.dto.BasketCalculationResult;
import digital.metro.pricing.calculator.dto.BasketEntry;

import java.math.BigDecimal;

public interface BasketCalculatorService {

    BasketCalculationResult calculateBasket(Basket basket);

    BigDecimal calculateArticle(BasketEntry be, String customerId);

}
