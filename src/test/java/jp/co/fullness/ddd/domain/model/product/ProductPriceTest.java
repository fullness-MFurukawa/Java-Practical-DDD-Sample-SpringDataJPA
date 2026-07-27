package jp.co.fullness.ddd.domain.model.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link ProductPrice}(商品単価の値オブジェクト)の単体テスト。
 *
 * <p>検証するドメインルール:
 * <ul>
 *   <li>必須(null不可)</li>
 *   <li>有効範囲は 50〜10000(境界値を含む)</li>
 *   <li>値による等価</li>
 * </ul>
 */
@DisplayName("ProductPrice(商品単価VO)")
class ProductPriceTest {

    /** 正常生成と境界値(50・10000)の確認。 */
    @Nested
    @DisplayName("正常系・境界値")
    class Valid {

        @Test
        @DisplayName("下限50で生成できる")
        void min() {
            assertEquals(50, ProductPrice.of(50).value());
        }

        @Test
        @DisplayName("上限10000で生成できる")
        void max() {
            assertEquals(10000, ProductPrice.of(10000).value());
        }
    }

    /** null・範囲外の拒否を確認。 */
    @Nested
    @DisplayName("異常系")
    class Invalid {

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            DomainException ex = assertThrows(DomainException.class, () -> ProductPrice.of(null));
            assertEquals("商品単価は必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("下限未満(49)は範囲エラー")
        void belowMin() {
            DomainException ex = assertThrows(DomainException.class, () -> ProductPrice.of(49));
            assertTrue(ex.getMessage().contains("50 以上 10000 以下"));
        }

        @Test
        @DisplayName("上限超過(10001)は範囲エラー")
        void aboveMax() {
            assertThrows(DomainException.class, () -> ProductPrice.of(10001));
        }
    }

    /** 値による等価の確認。 */
    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("同じ値は等価でhashCodeも一致")
        void equalsSameValue() {
            assertEquals(ProductPrice.of(500), ProductPrice.of(500));
            assertEquals(ProductPrice.of(500).hashCode(), ProductPrice.of(500).hashCode());
        }
    }
}
