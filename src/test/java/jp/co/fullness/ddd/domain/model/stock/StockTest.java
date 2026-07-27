package jp.co.fullness.ddd.domain.model.stock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link Stock}(在庫エンティティ)の単体テスト。
 *
 * <p>検証する仕様:
 * <ul>
 *   <li>createNew/restore による生成と不変条件(null不可)</li>
 *   <li>increase/decrease: 負数の拒否、加減算後も 0〜100 に収める(範囲外は例外)</li>
 *   <li>changeQuantity による差し替え(null拒否)</li>
 *   <li>isOutOfStock/isFullCapacity の判定</li>
 *   <li>同一性(StockId)による等価</li>
 * </ul>
 */
@DisplayName("Stock(在庫Entity)")
class StockTest {

    @Nested
    @DisplayName("生成")
    class Create {

        @Test
        @DisplayName("createNewはIDを採番し初期在庫を保持する")
        void createNew() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            assertNotNull(s.getStockId());
            assertEquals(10, s.getQuantity().value());
        }

        @Test
        @DisplayName("初期在庫nullは例外")
        void createNewNull() {
            DomainException ex = assertThrows(DomainException.class, () -> Stock.createNew(null));
            assertEquals("在庫数は必須です。", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("increase / decrease")
    class ChangeAmount {

        @Test
        @DisplayName("加算できる")
        void increase() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            s.increase(5);
            assertEquals(15, s.getQuantity().value());
        }

        @Test
        @DisplayName("減算できる")
        void decrease() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            s.decrease(8);
            assertEquals(2, s.getQuantity().value());
        }

        @Test
        @DisplayName("負数の加算は拒否")
        void increaseNegative() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            assertThrows(DomainException.class, () -> s.increase(-1));
        }

        @Test
        @DisplayName("上限超過となる加算は範囲エラー(100+1)")
        void increaseOverMax() {
            Stock s = Stock.createNew(StockQuantity.of(100));
            assertThrows(DomainException.class, () -> s.increase(1)); // 101はStockQuantityが拒否
        }

        @Test
        @DisplayName("下限未満となる減算は範囲エラー(0-1)")
        void decreaseBelowMin() {
            Stock s = Stock.createNew(StockQuantity.of(0));
            assertThrows(DomainException.class, () -> s.decrease(1)); // -1はStockQuantityが拒否
        }
    }

    @Nested
    @DisplayName("changeQuantity / 判定")
    class ChangeAndCheck {

        @Test
        @DisplayName("changeQuantityで差し替えできる")
        void change() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            s.changeQuantity(StockQuantity.of(30));
            assertEquals(30, s.getQuantity().value());
        }

        @Test
        @DisplayName("changeQuantityにnullは拒否")
        void changeNull() {
            Stock s = Stock.createNew(StockQuantity.of(10));
            assertThrows(DomainException.class, () -> s.changeQuantity(null));
        }

        @Test
        @DisplayName("在庫0は在庫切れ、100は満杯")
        void flags() {
            assertTrue(Stock.createNew(StockQuantity.of(0)).isOutOfStock());
            assertTrue(Stock.createNew(StockQuantity.of(100)).isFullCapacity());
        }
    }

    @Nested
    @DisplayName("同一性による等価")
    class Identity {

        @Test
        @DisplayName("IDが同じなら在庫数が違っても等価")
        void equalsById() {
            StockId id = StockId.createNew();
            Stock a = Stock.restore(id, StockQuantity.of(10));
            Stock b = Stock.restore(id, StockQuantity.of(20));
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }
    }
}
