package jp.co.fullness.ddd.domain.model.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * {@link Category}(商品カテゴリエンティティ)の単体テスト。
 *
 * <p>検証する仕様:
 * <ul>
 *   <li>createNew は新しいIDを採番し、指定の名称で生成する</li>
 *   <li>restore は指定IDで再構築する</li>
 *   <li>不変条件: id/name は null 不可</li>
 *   <li>rename で名称を変更でき、null は拒否する</li>
 *   <li>同一性(CategoryId)による等価: IDが同じなら名称が違っても等価</li>
 * </ul>
 */
@DisplayName("Category(カテゴリEntity)")
class CategoryTest {

    @Nested
    @DisplayName("生成")
    class Create {

        @Test
        @DisplayName("createNewはIDを採番し名称を保持する")
        void createNew() {
            Category c = Category.createNew(CategoryName.of("文房具"));
            assertNotNull(c.getCategoryId());               // IDが採番される
            assertEquals("文房具", c.getName().value());
        }

        @Test
        @DisplayName("restoreは指定IDで再構築する")
        void restore() {
            CategoryId id = CategoryId.createNew();
            Category c = Category.restore(id, CategoryName.of("雑貨"));
            assertEquals(id, c.getCategoryId());
        }
    }

    @Nested
    @DisplayName("不変条件")
    class Invariant {

        @Test
        @DisplayName("id が null なら例外")
        void nullId() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Category.restore(null, CategoryName.of("文房具")));
            assertEquals("カテゴリIDは必須です。", ex.getMessage());
        }

        @Test
        @DisplayName("name が null なら例外")
        void nullName() {
            assertThrows(DomainException.class,
                    () -> Category.restore(CategoryId.createNew(), null));
        }
    }

    @Nested
    @DisplayName("振る舞い")
    class Behavior {

        @Test
        @DisplayName("renameで名称を変更できる")
        void rename() {
            Category c = Category.createNew(CategoryName.of("文房具"));
            c.rename(CategoryName.of("事務用品"));
            assertEquals("事務用品", c.getName().value());
        }

        @Test
        @DisplayName("renameにnullは拒否")
        void renameNull() {
            Category c = Category.createNew(CategoryName.of("文房具"));
            assertThrows(DomainException.class, () -> c.rename(null));
        }
    }

    @Nested
    @DisplayName("同一性による等価")
    class Identity {

        @Test
        @DisplayName("IDが同じなら名称が違っても等価")
        void equalsById() {
            CategoryId id = CategoryId.createNew();
            Category a = Category.restore(id, CategoryName.of("文房具"));
            Category b = Category.restore(id, CategoryName.of("雑貨")); // 名称は異なる
            assertEquals(a, b);                       // 同一性(ID)で等価
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("IDが異なれば非等価")
        void notEqualsByDifferentId() {
            Category a = Category.createNew(CategoryName.of("文房具"));
            Category b = Category.createNew(CategoryName.of("文房具")); // 同名だがID別
            assertNotEquals(a, b);
        }
    }
}