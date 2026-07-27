package jp.co.fullness.ddd.application.mapper;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link ProductDTOAssembler} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>3つの Mapper をモック化し、Assembler 自身のロジック（DTO→集約の合成、
 * 集約→DTO の分解、null ガード、委譲）だけを検証する。
 * モックのフィールド名を Assembler のフィールド名に合わせることで、
 * 総称型（消去後は同一の DomainBiMapper 型）でも名前解決で正しく注入される。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDTOAssembler: DTO群 ⇔ Product集約 の合成/分解")
class ProductDTOAssemblerTest {

    @Mock
    private DomainBiMapper<ProductDTO, Product> productMapper;
    @Mock
    private DomainBiMapper<CategoryDTO, Category> categoryMapper;
    @Mock
    private DomainBiMapper<StockDTO, Stock> stockMapper;

    private ProductDTOAssembler assembler;

    @BeforeEach
    void setUp() {
        // 総称型が型消去されると Mockito は同一型の引数を判別できず順不同で注入してしまう。
        // それを避けるため、コンストラクタで明示的に正しい順序で注入する。
        assembler = new ProductDTOAssembler(productMapper, categoryMapper, stockMapper);
    }

    private ProductDTO fullDto() {
        return new ProductDTO("pid", "ペン", 120,
                new CategoryDTO("cid", "文房具"),
                new StockDTO("sid", 10));
    }

    @Nested
    @DisplayName("assembleDomain: DTO群 → Product集約")
    class AssembleDomain {

        @Test
        @DisplayName("骨格に Category と Stock を attach して合成する")
        void success() {
            ProductDTO dto = fullDto();
            Product skeleton = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("ペン"), ProductPrice.of(120));
            Category category = Category.createNew(CategoryName.of("文房具"));
            Stock stock = Stock.createNew(StockQuantity.of(10));

            when(productMapper.toDomain(any())).thenReturn(skeleton);
            when(categoryMapper.toDomain(any())).thenReturn(category);
            when(stockMapper.toDomain(any())).thenReturn(stock);

            Product result = assembler.assembleDomain(dto);

            assertSame(skeleton, result);
            assertSame(category, result.getCategory());
            assertSame(stock, result.getStock());
        }

        @Test
        @DisplayName("ProductDTO が null なら例外")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> assembler.assembleDomain(null));
        }

        @Test
        @DisplayName("CategoryDTO が null なら例外")
        void nullCategory() {
            ProductDTO dto = new ProductDTO("pid", "ペン", 120, null, new StockDTO("sid", 10));
            assertThrows(InvalidInputException.class, () -> assembler.assembleDomain(dto));
        }

        @Test
        @DisplayName("StockDTO が null なら例外")
        void nullStock() {
            ProductDTO dto = new ProductDTO("pid", "ペン", 120, new CategoryDTO("cid", "文房具"), null);
            assertThrows(InvalidInputException.class, () -> assembler.assembleDomain(dto));
        }
    }

    @Nested
    @DisplayName("assembleDto: Product集約 → ネストDTO")
    class AssembleDto {

        @Test
        @DisplayName("骨格DTOにカテゴリ・在庫DTOを合成する")
        void success() {
            Product domain = Product.createNew(
                    ProductName.of("ペン"), ProductPrice.of(120),
                    Category.createNew(CategoryName.of("文房具")),
                    StockQuantity.of(10));

            ProductDTO skeletonDto = new ProductDTO("pid", "ペン", 120, null, null);
            CategoryDTO catDto = new CategoryDTO("cid", "文房具");
            StockDTO stockDto = new StockDTO("sid", 10);

            when(productMapper.fromDomain(any())).thenReturn(skeletonDto);
            when(categoryMapper.fromDomain(any())).thenReturn(catDto);
            when(stockMapper.fromDomain(any())).thenReturn(stockDto);

            ProductDTO result = assembler.assembleDto(domain);

            assertSame(skeletonDto, result);
            assertSame(catDto, result.getCategory());
            assertSame(stockDto, result.getStock());
        }

        @Test
        @DisplayName("null なら例外")
        void nullDomain() {
            assertThrows(InvalidInputException.class, () -> assembler.assembleDto(null));
        }
    }

    @Nested
    @DisplayName("toCategoryDto")
    class ToCategoryDto {

        @Test
        @DisplayName("Category を CategoryDTO に変換する（categoryMapper へ委譲）")
        void success() {
            Category category = Category.createNew(CategoryName.of("文房具"));
            CategoryDTO catDto = new CategoryDTO("cid", "文房具");
            when(categoryMapper.fromDomain(any())).thenReturn(catDto);

            assertSame(catDto, assembler.toCategoryDto(category));
        }

        @Test
        @DisplayName("null なら例外")
        void nullCategory() {
            assertThrows(InvalidInputException.class, () -> assembler.toCategoryDto(null));
        }
    }
}