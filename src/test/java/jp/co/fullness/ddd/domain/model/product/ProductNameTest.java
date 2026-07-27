package jp.co.fullness.ddd.domain.model.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link ProductName}(商品名の値オブジェクト)の単体テスト。
 *
 * <p>検証するドメインルール:
 * <ul>
 *   <li>必須(null/空/空白のみ不可)</li>
 *   <li>最大30文字(境界値を含む)</li>
 *   <li>前後空白はトリムして保持</li>
 *   <li>値による等価</li>
 * </ul>
 */
@DisplayName("ProductName(商品名VO)")
class ProductNameTest {

    @Nested
    @DisplayName("正常系・境界値")
    class Valid {

        @Test
        @DisplayName("通常の名称で生成できる")
        void normal() {
            assertEquals("万年筆", ProductName.of("万年筆").value());
        }

        @Test
        @DisplayName("前後の空白はトリムされる")
        void trims() {
            assertEquals("万年筆", ProductName.of("  万年筆  ").value());
        }

        @Test
        @DisplayName("最大長30文字ちょうどは許可")
        void maxLength() {
            String name = "a".repeat(30); // 上限ちょうど
            assertEquals(name, ProductName.of(name).value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class Invalid {

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            DomainException ex = assertThrows(DomainException.class, () -> ProductName.of(null));
            assertEquals("商品名は必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("空白のみは空エラー")
        void blank() {
            DomainException ex = assertThrows(DomainException.class, () -> ProductName.of("   "));
            assertEquals("商品名は空にできません。", ex.getMessage());
        }

        @Test
        @DisplayName("31文字は最大長エラー")
        void tooLong() {
            String name = "a".repeat(31); // 上限+1
            DomainException ex = assertThrows(DomainException.class, () -> ProductName.of(name));
            assertTrue(ex.getMessage().contains("30文字以内"));
        }
    }

    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("同じ値は等価")
        void equalsSameValue() {
            assertEquals(ProductName.of("ノート"), ProductName.of("ノート"));
            assertEquals(ProductName.of("ノート").hashCode(), ProductName.of("ノート").hashCode());
        }
    }
}
