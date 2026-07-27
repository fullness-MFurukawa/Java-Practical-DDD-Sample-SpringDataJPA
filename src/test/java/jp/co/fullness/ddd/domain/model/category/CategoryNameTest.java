package jp.co.fullness.ddd.domain.model.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link CategoryName}(カテゴリ名の値オブジェクト)の単体テスト。
 *
 * <p>検証するドメインルール:
 * <ul>
 *   <li>必須(null/空/空白のみ不可)</li>
 *   <li>最大20文字(境界値を含む)</li>
 *   <li>前後空白はトリムして保持</li>
 *   <li>値による等価</li>
 * </ul>
 */
@DisplayName("CategoryName(カテゴリ名VO)")
class CategoryNameTest {

    @Nested
    @DisplayName("正常系・境界値")
    class Valid {

        @Test
        @DisplayName("通常の名称で生成できる")
        void normal() {
            assertEquals("文房具", CategoryName.of("文房具").value());
        }

        @Test
        @DisplayName("前後の空白はトリムされる")
        void trims() {
            assertEquals("文房具", CategoryName.of("  文房具  ").value());
        }

        @Test
        @DisplayName("最大長20文字ちょうどは許可")
        void maxLength() {
            String name = "a".repeat(20); // 上限ちょうど
            assertEquals(name, CategoryName.of(name).value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class Invalid {

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            DomainException ex = assertThrows(DomainException.class, () -> CategoryName.of(null));
            assertEquals("カテゴリ名は必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("空白のみは空エラー")
        void blank() {
            DomainException ex = assertThrows(DomainException.class, () -> CategoryName.of("   "));
            assertEquals("カテゴリ名は空にできません。", ex.getMessage());
        }

        @Test
        @DisplayName("21文字は最大長エラー")
        void tooLong() {
            String name = "a".repeat(21); // 上限+1
            DomainException ex = assertThrows(DomainException.class, () -> CategoryName.of(name));
            assertTrue(ex.getMessage().contains("20文字以内"));
        }
    }

    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("同じ値は等価")
        void equalsSameValue() {
            assertEquals(CategoryName.of("雑貨"), CategoryName.of("雑貨"));
            assertEquals(CategoryName.of("雑貨").hashCode(), CategoryName.of("雑貨").hashCode());
        }
    }
}
