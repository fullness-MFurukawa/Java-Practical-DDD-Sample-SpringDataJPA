package jp.co.fullness.ddd.infrastructure.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;
import jp.co.fullness.ddd.infrastructure.category.CategoryEntityMapper;
import jp.co.fullness.ddd.infrastructure.category.ProductCategoryEntity;
import jp.co.fullness.ddd.infrastructure.stock.ProductStockEntity;
import jp.co.fullness.ddd.infrastructure.stock.StockEntityMapper;

/**
 * {@link ProductAssembler} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>各 EntityMapper をモック化し、Assembler 自身のロジック（entity グラフからの
 * skeleton + attach 合成、null ガード、分解メソッドの委譲）だけを検証する。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductAssembler: Entity ⇔ Product集約 の合成/分解")
class ProductAssemblerTest {

    @Mock
    private ProductEntityMapper productEntityMapper;
    @Mock
    private CategoryEntityMapper categoryEntityMapper;
    @Mock
    private StockEntityMapper stockEntityMapper;

    @InjectMocks
    private ProductAssembler assembler;

    private Category sampleCategory() {
        return Category.createNew(CategoryName.of("文房具"));
    }

    private Product sampleSkeleton() {
        return Product.restoreSkeleton(
                ProductId.createNew(),
                ProductName.of("油性ボールペン"),
                ProductPrice.of(120));
    }

    private Product sampleFullProduct() {
        return Product.createNew(
                ProductName.of("油性ボールペン"),
                ProductPrice.of(120),
                sampleCategory(),
                StockQuantity.of(80));
    }

    /** category / stock を関連づけた ProductEntity を組み立てる */
    private ProductEntity entityWithRelations() {
        ProductEntity e = new ProductEntity();
        e.setCategory(new ProductCategoryEntity());
        e.assignStock(new ProductStockEntity());
        return e;
    }

    @Nested
    @DisplayName("assemble: Entity → Product集約 の合成")
    class Assemble {

        @Test
        @DisplayName("骨格に Category と Stock を attach して合成する")
        void success() {
            Product skeleton = sampleSkeleton();
            Category category = sampleCategory();
            Stock stock = Stock.createNew(StockQuantity.of(80));

            when(productEntityMapper.toDomain(any())).thenReturn(skeleton);
            when(categoryEntityMapper.toDomain(any())).thenReturn(category);
            when(stockEntityMapper.toDomain(any())).thenReturn(stock);

            Product result = assembler.assemble(entityWithRelations());

            assertSame(skeleton, result);
            assertSame(category, result.getCategory());
            assertSame(stock, result.getStock());
        }

        @Test
        @DisplayName("Entity が null なら例外（Mapper は呼ばれない）")
        void nullEntity() {
            assertThrows(DomainException.class, () -> assembler.assemble(null));
        }

        @Test
        @DisplayName("category が紐づいていないなら例外")
        void nullCategory() {
            ProductEntity e = new ProductEntity();
            e.assignStock(new ProductStockEntity());   // stock はあるが category が null
            assertThrows(DomainException.class, () -> assembler.assemble(e));
        }

        @Test
        @DisplayName("stock が紐づいていないなら例外")
        void nullStock() {
            ProductEntity e = new ProductEntity();
            e.setCategory(new ProductCategoryEntity());   // category はあるが stock が null
            assertThrows(DomainException.class, () -> assembler.assemble(e));
        }
    }

    @Nested
    @DisplayName("分解: Product集約 → Entity")
    class Decompose {

        @Test
        @DisplayName("toProductEntity は ProductEntityMapper.fromDomain に委譲する")
        void toProductEntity_delegates() {
            Product product = sampleFullProduct();
            ProductEntity expected = new ProductEntity();
            when(productEntityMapper.fromDomain(product)).thenReturn(expected);

            assertSame(expected, assembler.toProductEntity(product));
        }

        @Test
        @DisplayName("toProductEntity は null なら例外")
        void toProductEntity_null() {
            assertThrows(DomainException.class, () -> assembler.toProductEntity(null));
        }

        @Test
        @DisplayName("toStockEntity は Product の Stock を取り出して委譲する")
        void toStockEntity_delegates() {
            Product product = sampleFullProduct();
            ProductStockEntity expected = new ProductStockEntity();
            when(stockEntityMapper.fromDomain(product.getStock())).thenReturn(expected);

            assertSame(expected, assembler.toStockEntity(product));
        }

        @Test
        @DisplayName("toStockEntity は Stock 未設定（骨格）なら例外")
        void toStockEntity_noStock() {
            assertThrows(DomainException.class, () -> assembler.toStockEntity(sampleSkeleton()));
        }

        @Test
        @DisplayName("extractCategoryUuid は Category の UUID 文字列を返す")
        void extractCategoryUuid_success() {
            Category category = sampleCategory();
            Product product = Product.createNew(
                    ProductName.of("油性ボールペン"),
                    ProductPrice.of(120),
                    category,
                    StockQuantity.of(80));

            assertEquals(category.getCategoryId().value(), assembler.extractCategoryUuid(product));
        }

        @Test
        @DisplayName("extractCategoryUuid は Category 未設定（骨格）なら例外")
        void extractCategoryUuid_noCategory() {
            assertThrows(DomainException.class, () -> assembler.extractCategoryUuid(sampleSkeleton()));
        }
    }
}