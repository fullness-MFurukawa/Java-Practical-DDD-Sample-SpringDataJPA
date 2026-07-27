package jp.co.fullness.ddd.infrastructure.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockId;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link StockEntityMapper} の単体テスト（Spring コンテナ経由）。
 *
 * <p>双方向（{@code toDomain} / {@code fromDomain}）を検証する。
 * {@code fromDomain} は関連 product（外部キー）を設定しないことも確認する。</p>
 */
@SpringBootTest
@DisplayName("StockEntityMapper: JPA Entity ⇔ Stock の相互変換（DI 経由）")
class StockEntityMapperTest {

    @Autowired
    private StockEntityMapper mapper;

    private static final String UUID_STR = "22222222-2222-2222-2222-222222222222";

    private ProductStockEntity entity(String stockUuid, Integer stock) {
        ProductStockEntity e = new ProductStockEntity();
        e.setStockUuid(stockUuid);
        e.setStock(stock);
        return e;
    }

    @Nested
    @DisplayName("toDomain: Entity → Stock")
    class ToDomain {

        @Test
        @DisplayName("有効な Entity を Stock に変換できる")
        void valid() {
            Stock stock = mapper.toDomain(entity(UUID_STR, 50));

            assertEquals(UUID_STR, stock.getStockId().value());
            assertEquals(50, stock.getQuantity().value().intValue());
        }

        @Test
        @DisplayName("Entity が null なら例外")
        void nullEntity() {
            assertThrows(DomainException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("stock_uuid が空白なら例外")
        void blankUuid() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity("  ", 50)));
        }

        @Test
        @DisplayName("在庫数が null なら例外")
        void nullQuantity() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(UUID_STR, null)));
        }

        @Test
        @DisplayName("在庫数が範囲外（100 超）なら例外（VO のバリデーション）")
        void outOfRangeQuantity() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(UUID_STR, 101)));
        }
    }

    @Nested
    @DisplayName("fromDomain: Stock → Entity")
    class FromDomain {

        @Test
        @DisplayName("Stock を Entity に変換できる（product は未設定）")
        void valid() {
            Stock stock = Stock.restore(StockId.fromString(UUID_STR), StockQuantity.of(30));

            ProductStockEntity entity = mapper.fromDomain(stock);

            assertEquals(UUID_STR, entity.getStockUuid());
            assertEquals(30, entity.getStock().intValue());
            // 関連 product（外部キー）は Mapper では設定しない（Repository が設定する）
            assertNull(entity.getProduct());
        }

        @Test
        @DisplayName("Stock が null なら例外")
        void nullDomain() {
            assertThrows(DomainException.class, () -> mapper.fromDomain(null));
        }
    }
}