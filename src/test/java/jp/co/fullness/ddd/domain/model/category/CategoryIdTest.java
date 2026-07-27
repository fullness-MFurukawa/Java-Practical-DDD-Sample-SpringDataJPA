package jp.co.fullness.ddd.domain.model.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link CategoryId}(カテゴリ識別子の値オブジェクト)の単体テスト。
 *
 * <p>検証するドメインルール:
 * <ul>
 *   <li>createNew は canonical(小文字・ハイフン付き36文字)なUUIDを一意に発行する</li>
 *   <li>fromString は必須・UUID形式を検証し、大文字入力は小文字へ正規化する</li>
 *   <li>正規化後の値が同じなら等価(大小文字の違いを吸収する)</li>
 * </ul>
 */
@DisplayName("CategoryId(カテゴリID VO)")
class CategoryIdTest {

    /** 新規採番(createNew)の形式と一意性を確認する。 */
    @Nested
    @DisplayName("createNew")
    class CreateNew {

        @Test
        @DisplayName("canonical(小文字・ハイフン付き36文字)で生成される")
        void generatesCanonical() {
            // 生成される内部表現が常にcanonical形式であることを保証
            String v = CategoryId.createNew().value();
            assertEquals(36, v.length());
            assertEquals(v.toLowerCase(), v);            // 小文字であること
            assertTrue(v.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
        }

        @Test
        @DisplayName("生成の都度、異なるIDになる")
        void unique() {
            // 識別子としての一意性(採番のたびに別値)
            assertNotEquals(CategoryId.createNew().value(), CategoryId.createNew().value());
        }
    }

    /** 既存値からの復元(fromString)の正規化と検証を確認する。 */
    @Nested
    @DisplayName("fromString")
    class FromString {

        @Test
        @DisplayName("大文字入力は小文字に正規化される")
        void normalizesToLowerCase() {
            // 入力は大小問わず受け付けるが、内部表現はcanonical(小文字)に統一
            String upper = "0F8FAD5B-D9CB-469F-A165-70867728950E";
            CategoryId id = CategoryId.fromString(upper);
            assertEquals(upper.toLowerCase(), id.value());
        }

        @Test
        @DisplayName("前後空白はトリムされる")
        void trims() {
            // 余分な空白は除去してから検証・保持する
            String raw = "  0f8fad5b-d9cb-469f-a165-70867728950e  ";
            assertEquals("0f8fad5b-d9cb-469f-a165-70867728950e", CategoryId.fromString(raw).value());
        }

        @Test
        @DisplayName("nullは必須エラー")
        void nullValue() {
            // 必須ルール。メッセージも仕様として検証
            DomainException ex = assertThrows(DomainException.class, () -> CategoryId.fromString(null));
            assertEquals("CategoryId は必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("空白のみは必須エラー")
        void blank() {
            // 空白のみは「未入力」と同義でルール違反
            assertThrows(DomainException.class, () -> CategoryId.fromString("   "));
        }

        @Test
        @DisplayName("UUID形式でない文字列はエラー")
        void invalidFormat() {
            // 8-4-4-4-12形式に合致しない値は受け付けない
            assertThrows(DomainException.class, () -> CategoryId.fromString("not-a-uuid"));
        }
    }

    /** 正規化を踏まえた値等価を確認する。 */
    @Nested
    @DisplayName("等価性")
    class Equality {

        @Test
        @DisplayName("大小文字違いでも正規化後に等価")
        void equalsAfterNormalize() {
            // 入力の大小文字が違っても、正規化後の値が同じなら等価になる
            CategoryId a = CategoryId.fromString("0F8FAD5B-D9CB-469F-A165-70867728950E");
            CategoryId b = CategoryId.fromString("0f8fad5b-d9cb-469f-a165-70867728950e");
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }
    }
}

