package jp.co.fullness.ddd.domain.model.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link Product}(商品エンティティ・集約ルート)の単体テスト。
 *
 * <p>検証する仕様:
 * <ul>
 *   <li>createNew は Category と Stock を含む完全な集約を生成する</li>
 *   <li>不変条件: id/name/price は null 不可</li>
 *   <li>集約の完全性: Category と Stock は「両方指定」か「両方null(骨格)」のみ許可</li>
 *   <li>restoreSkeleton + attachCategory/attachStock による段階的な合成</li>
 *   <li>骨格状態での currentStock/changeStock は明示的に例外(nullガード)</li>
 *   <li>同一性(ProductId)による等価</li>
 * </ul>
 */
@DisplayName("Product(商品Entity・集約ルート)")
class ProductTest {

    /** テスト用の有効なカテゴリを生成するヘルパ。 */
    private Category validCategory() {
        return Category.createNew(CategoryName.of("文房具"));
    }

    @Nested
    @DisplayName("生成(完全な集約)")
    class CreateNew {

        @Test
        @DisplayName("createNewでカテゴリ・在庫を含む商品を生成する")
        void createNew() {
            Product p = Product.createNew(
                    ProductName.of("万年筆"), ProductPrice.of(3000),
                    validCategory(), StockQuantity.of(10));
            assertNotNull(p.getProductId());
            assertEquals("万年筆", p.getName().value());
            assertEquals(3000, p.getPrice().value());
            assertNotNull(p.getCategory());
            assertEquals(10, p.currentStock().value()); // 在庫が合成されている
        }
    }

    @Nested
    @DisplayName("不変条件・集約の完全性")
    class Invariant {

        @Test
        @DisplayName("nameがnullなら例外")
        void nullName() {
            assertThrows(DomainException.class, () -> Product.createNew(
                    null, ProductPrice.of(3000), validCategory(), StockQuantity.of(10)));
        }

        @Test
        @DisplayName("Categoryのみ指定・Stockなしの再構築は完全性エラー")
        void onlyCategoryProvided() {
            // XOR完全性チェック: 片方だけの指定は許可しない
            DomainException ex = assertThrows(DomainException.class, () -> Product.restore(
                    ProductId.createNew(), ProductName.of("万年筆"), ProductPrice.of(3000),
                    validCategory(), /* stock */ null));
            assertTrue(ex.getMessage().contains("両方"));
        }
    }

    @Nested
    @DisplayName("骨格再構築(restoreSkeleton)と合成(attach)")
    class Skeleton {

        @Test
        @DisplayName("骨格生成後にattachで合成できる")
        void skeletonThenAttach() {
            Product p = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("万年筆"), ProductPrice.of(3000));
            assertNull(p.getCategory());            // 骨格ではまだ未設定
            p.attachCategory(validCategory());
            p.attachStock(Stock.createNew(StockQuantity.of(5)));
            assertNotNull(p.getCategory());
            assertEquals(5, p.currentStock().value());
        }

        @Test
        @DisplayName("在庫未設定でcurrentStockを呼ぶと明示的な例外")
        void currentStockWithoutAttach() {
            Product p = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("万年筆"), ProductPrice.of(3000));
            DomainException ex = assertThrows(DomainException.class, p::currentStock);
            assertTrue(ex.getMessage().contains("在庫が未設定"));
        }

        @Test
        @DisplayName("attachCategoryにnullは拒否")
        void attachNull() {
            Product p = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("万年筆"), ProductPrice.of(3000));
            assertThrows(DomainException.class, () -> p.attachCategory(null));
        }
    }

    @Nested
    @DisplayName("同一性による等価")
    class Identity {

        @Test
        @DisplayName("IDが同じなら属性が違っても等価")
        void equalsById() {
            ProductId id = ProductId.createNew();
            Product a = Product.restore(id, ProductName.of("万年筆"), ProductPrice.of(3000),
                    validCategory(), Stock.createNew(StockQuantity.of(1)));
            Product b = Product.restore(id, ProductName.of("鉛筆"), ProductPrice.of(100),
                    validCategory(), Stock.createNew(StockQuantity.of(2)));
            assertEquals(a, b);                     // 同一性(ProductId)で等価
            assertEquals(a.hashCode(), b.hashCode());
        }
    }
}