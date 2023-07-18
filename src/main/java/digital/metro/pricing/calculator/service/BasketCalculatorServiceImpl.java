package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.repository.PriceRepository;
import digital.metro.pricing.calculator.dto.Basket;
import digital.metro.pricing.calculator.dto.BasketCalculationResult;
import digital.metro.pricing.calculator.dto.BasketEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BasketCalculatorServiceImpl implements BasketCalculatorService{

    private final PriceRepository priceRepository;

    @Autowired
    public BasketCalculatorServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public BasketCalculationResult calculateBasket(Basket basket) {
        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticle(entry, basket.getCustomerId())));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    public BigDecimal calculateArticle(BasketEntry be, String customerId) {
        String articleId = be.getArticleId();
        BigDecimal quantity = be.getQuantity();

        if (quantity == null || BigDecimal.ZERO.equals(quantity)) {
            return BigDecimal.ZERO;
        }

        BigDecimal customerPrice = null;
        if (customerId != null) {
             customerPrice = priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId);
        }
        if (customerPrice == null) {
            customerPrice = priceRepository.getPriceByArticleId(articleId);
        }

        return customerPrice.multiply(quantity);
    }
}
