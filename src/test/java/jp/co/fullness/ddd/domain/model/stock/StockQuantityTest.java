package jp.co.fullness.ddd.domain.model.stock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link StockQuantity}(在庫数の値オブジェクト)の単体テスト。
 *
 * <p>検証するドメインルール:
 * <ul>
 *   <li>必須(null不可)</li>
 *   <li>有効範囲は 0〜100(境界値を含む)</li>
 *   <li>値による等価(同じ数量なら equals/hashCode が一致)</li>
 * </ul>
 * テスト自体を「在庫数の仕様書」として読めることを意図している。
 */
@DisplayName("StockQuantity(在庫数VO)")
class StockQuantityTest {

    /** 正常に生成できるケースと、有効範囲の境界値(0・100)を確認する。 */
    @Nested
    @DisplayName("正常系・境界値")
    class Valid {

        @Test
        @DisplayName("最小値0で生成できる")
        void createMin() {
            // 境界値の下限。0は「在庫切れ」を表す有効値
            StockQuantity q = StockQuantity.of(0);
            assertEquals(0, q.value());
        }

        @Test
        @DisplayName("最大値100で生成できる")
        void createMax() {
            // 境界値の上限。100は「満杯」を表す有効値
            StockQuantity q = StockQuantity.of(100);
            assertEquals(100, q.value());
        }

        @Test
        @DisplayName("中間値で生成できる")
        void createMiddle() {
            // 範囲内の代表値
            assertEquals(50, StockQuantity.of(50).value());
        }
    }

    /** ドメインルール違反(null・範囲外)で DomainException になることを確認する。 */
    @Nested
    @DisplayName("異常系")
    class Invalid {

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            // 必須ルール違反。メッセージも仕様として固定で検証する
            DomainException ex = assertThrows(DomainException.class, () -> StockQuantity.of(null));
            assertEquals("在庫数は必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("下限未満(-1)は範囲エラー")
        void belowMin() {
            // 境界の1つ外側(下限-1)。範囲メッセージを含むことを確認
            DomainException ex = assertThrows(DomainException.class, () -> StockQuantity.of(-1));
            assertTrue(ex.getMessage().contains("0 以上 100 以下"));
        }

        @Test
        @DisplayName("上限超過(101)は範囲エラー")
        void aboveMax() {
            // 境界の1つ外側(上限+1)
            assertThrows(DomainException.class, () -> StockQuantity.of(101));
        }
    }

    /** 値オブジェクトの本質である「値による等価」を確認する。 */
    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("同じ値は等価でhashCodeも一致")
        void equalsSameValue() {
            // 値が同じなら同一インスタンスでなくても等価であること
            StockQuantity a = StockQuantity.of(10);
            StockQuantity b = StockQuantity.of(10);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode()); // equals一致ならhashCodeも一致(規約)
        }

        @Test
        @DisplayName("異なる値は非等価")
        void notEqualsDifferentValue() {
            assertNotEquals(StockQuantity.of(10), StockQuantity.of(20));
        }
    }
}