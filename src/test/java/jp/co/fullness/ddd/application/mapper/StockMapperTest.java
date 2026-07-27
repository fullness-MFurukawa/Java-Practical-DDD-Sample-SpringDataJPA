package jp.co.fullness.ddd.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockId;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link StockMapper}（StockDTO ⇔ Stock）の単体テスト（DI 経由）。
 */
@SpringBootTest
@DisplayName("StockMapper: StockDTO ⇔ Stock の相互変換")
class StockMapperTest {

    @Autowired
    private StockMapper mapper;

    private static final String UUID_STR = "22222222-2222-2222-2222-222222222222";

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("id 指定ありなら restore（そのIDで復元）")
        void withId_restore() {
            Stock s = mapper.toDomain(new StockDTO(UUID_STR, 50));
            assertEquals(UUID_STR, s.getStockId().value());
            assertEquals(50, s.getQuantity().value().intValue());
        }

        @Test
        @DisplayName("id 未指定なら createNew（新規採番・IDが振られる）")
        void withoutId_createNew() {
            Stock s = mapper.toDomain(new StockDTO(null, 50));
            assertNotNull(s.getStockId().value());
            assertEquals(50, s.getQuantity().value().intValue());
        }

        @Test
        @DisplayName("DTO が null なら例外")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("quantity が null なら例外")
        void nullQuantity() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(new StockDTO(UUID_STR, null)));
        }
    }

    @Nested
    @DisplayName("fromDomain")
    class FromDomain {

        @Test
        @DisplayName("Stock を StockDTO に変換できる")
        void valid() {
            Stock s = Stock.restore(StockId.fromString(UUID_STR), StockQuantity.of(30));

            StockDTO dto = mapper.fromDomain(s);

            assertEquals(UUID_STR, dto.getId());
            assertEquals(30, dto.getQuantity().intValue());
        }

        @Test
        @DisplayName("null なら例外")
        void nullDomain() {
            assertThrows(InvalidInputException.class, () -> mapper.fromDomain(null));
        }
    }
}