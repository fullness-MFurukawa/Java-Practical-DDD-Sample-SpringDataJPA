package jp.co.fullness.ddd.domain.model.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link ProductId}(商品識別子の値オブジェクト)の単体テスト。
 * CategoryId と同型(createNew/fromString、canonical正規化、値等価)。
 */
@DisplayName("ProductId(商品ID VO)")
class ProductIdTest {

    @Nested
    @DisplayName("createNew")
    class CreateNew {

        @Test
        @DisplayName("canonicalなUUIDを一意に発行する")
        void generatesUniqueCanonical() {
            String v = ProductId.createNew().value();
            assertTrue(v.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
            assertNotEquals(ProductId.createNew().value(), ProductId.createNew().value());
        }
    }

    @Nested
    @DisplayName("fromString")
    class FromString {

        @Test
        @DisplayName("大文字入力は小文字へ正規化される")
        void normalizes() {
            String upper = "0F8FAD5B-D9CB-469F-A165-70867728950E";
            assertEquals(upper.toLowerCase(), ProductId.fromString(upper).value());
        }

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            DomainException ex = assertThrows(DomainException.class, () -> ProductId.fromString(null));
            assertEquals("ProductIdは必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("UUID形式でない値はエラー")
        void invalid() {
            assertThrows(DomainException.class, () -> ProductId.fromString("xxxx"));
        }
    }

    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("大小文字違いでも正規化後に等価")
        void equalsAfterNormalize() {
            ProductId a = ProductId.fromString("0F8FAD5B-D9CB-469F-A165-70867728950E");
            ProductId b = ProductId.fromString("0f8fad5b-d9cb-469f-a165-70867728950e");
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }
    }
}

