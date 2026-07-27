package jp.co.fullness.ddd.infrastructure.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link ProductEntityMapper} の単体テスト（Spring コンテナ経由）。
 *
 * <p>{@code toDomain} は骨格（category/stock=null）の {@link Product} を返すこと、
 * {@code fromDomain} は category/stock/id を設定しないことを検証する。</p>
 */
@SpringBootTest
@DisplayName("ProductEntityMapper: JPA Entity ⇔ Product（骨格）の相互変換（DI 経由）")
class ProductEntityMapperTest {

    @Autowired
    private ProductEntityMapper mapper;

    private static final String UUID_STR = "33333333-3333-3333-3333-333333333333";

    private ProductEntity entity(String productUuid, String name, Integer price) {
        ProductEntity e = new ProductEntity();
        e.setProductUuid(productUuid);
        e.setName(name);
        e.setPrice(price);
        return e;
    }

    @Nested
    @DisplayName("toDomain: Entity → Product（骨格）")
    class ToDomain {

        @Test
        @DisplayName("有効な Entity を骨格 Product に変換できる（カテゴリ・在庫は null）")
        void valid() {
            Product product = mapper.toDomain(entity(UUID_STR, "油性ボールペン", 120));

            assertEquals(UUID_STR, product.getProductId().value());
            assertEquals("油性ボールペン", product.getName().value());
            assertEquals(120, product.getPrice().value().intValue());
            assertNull(product.getCategory());
            assertNull(product.getStock());
        }

        @Test
        @DisplayName("Entity が null なら例外")
        void nullEntity() {
            assertThrows(DomainException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("product_uuid が空白なら例外")
        void blankUuid() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity("  ", "商品", 120)));
        }

        @Test
        @DisplayName("name が空白なら例外")
        void blankName() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(UUID_STR, "  ", 120)));
        }

        @Test
        @DisplayName("price が null なら例外")
        void nullPrice() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(UUID_STR, "商品", null)));
        }

        @Test
        @DisplayName("price が範囲外（50 未満）なら例外（VO のバリデーション）")
        void outOfRangePrice() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(UUID_STR, "商品", 10)));
        }
    }

    @Nested
    @DisplayName("fromDomain: Product → Entity")
    class FromDomain {

        @Test
        @DisplayName("Product を Entity に変換できる（category/stock/id は未設定）")
        void valid() {
            Category category = Category.createNew(CategoryName.of("文房具"));
            Product product = Product.createNew(
                    ProductName.of("油性ボールペン"),
                    ProductPrice.of(120),
                    category,
                    StockQuantity.of(80));

            ProductEntity entity = mapper.fromDomain(product);

            assertEquals(product.getProductId().value(), entity.getProductUuid());
            assertEquals("油性ボールペン", entity.getName());
            assertEquals(120, entity.getPrice().intValue());
            // 関連・主キーは Mapper では設定しない（Repository/Assembler が設定する）
            assertNull(entity.getCategory());
            assertNull(entity.getStock());
            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Product が null なら例外")
        void nullDomain() {
            assertThrows(DomainException.class, () -> mapper.fromDomain(null));
        }
    }
}
