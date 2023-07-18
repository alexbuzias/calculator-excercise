package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.dto.Basket;
import digital.metro.pricing.calculator.dto.BasketCalculationResult;
import digital.metro.pricing.calculator.dto.BasketEntry;
import digital.metro.pricing.calculator.repository.PriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class BasketCalculatorServiceTest {

    @InjectMocks
    private BasketCalculatorServiceImpl service;

    @Mock
    private PriceRepository mockPriceRepository;

    private static Stream<Arguments> provideBasketData() {
        return Stream.of(
                Arguments.of("c1", new BigDecimal("3.25"), new BigDecimal("2"), new BigDecimal("6.5")),
                Arguments.of("c2", new BigDecimal("31.7"), new BigDecimal("4"), new BigDecimal("137.16")),
                Arguments.of(null, new BigDecimal("4.75"), new BigDecimal("2"), new BigDecimal("68.58")),
                Arguments.of("c1", new BigDecimal("4.75"), null, BigDecimal.ZERO),
                Arguments.of("c2", new BigDecimal("4.75"), BigDecimal.ZERO, BigDecimal.ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource("provideBasketData")
    public void testCalculateArticle(String customerId, BigDecimal customerPrice, BigDecimal quantity, BigDecimal expectedTotal) {
        // GIVEN
        String articleId = "article-1";
        BigDecimal price = new BigDecimal("34.29");
        Mockito.lenient().when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(price);
        Mockito.lenient().when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, "c1")).thenReturn(customerPrice);

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, quantity), customerId);

        // THEN
        Assertions.assertThat(result).isEqualByComparingTo(expectedTotal);
    }

    @Test
    public void testCalculateBasket() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        Mockito.when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(prices.get("article-1"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-2")).thenReturn(prices.get("article-2"));
        Mockito.when(mockPriceRepository.getPriceByArticleId("article-3")).thenReturn(prices.get("article-3"));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        Assertions.assertThat(result.getCustomerId()).isEqualTo("customer-1");
        Assertions.assertThat(result.getPricedBasketEntries()).isEqualTo(prices);
        Assertions.assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("11.78"));
    }
}
