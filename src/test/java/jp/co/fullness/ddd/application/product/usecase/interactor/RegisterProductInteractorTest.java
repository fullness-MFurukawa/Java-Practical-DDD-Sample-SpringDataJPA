package jp.co.fullness.ddd.application.product.usecase.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.category.service.CategoryService;
import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;

/**
 * {@link RegisterProductInteractor} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>Service / Assembler をモック化し、Interactor のオーケストレーション（委譲・変換・
 * 重複チェック・null ガード）だけを検証する。修正版 addProduct（重複チェックを含む）を前提とする。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterProductInteractor: 商品登録ユースケース")
class RegisterProductInteractorTest {

    @Mock
    private ProductService productService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ProductDTOAssembler assembler;

    @InjectMocks
    private RegisterProductInteractor interactor;

    private static final String CAT_UUID = "11111111-1111-1111-1111-111111111111";

    @Nested
    @DisplayName("getCategories")
    class GetCategories {

        @Test
        @DisplayName("Category をDTOに変換したリストを返す")
        void returnsMappedDtos() {
            Category c = Category.createNew(CategoryName.of("文房具"));
            CategoryDTO dto = new CategoryDTO("cid", "文房具");
            when(categoryService.getCategories()).thenReturn(List.of(c));
            when(assembler.toCategoryDto(c)).thenReturn(dto);

            List<CategoryDTO> result = interactor.getCategories();

            assertEquals(1, result.size());
            assertSame(dto, result.get(0));
        }
    }

    @Nested
    @DisplayName("getCategoryById")
    class GetCategoryById {

        @Test
        @DisplayName("UUID文字列からカテゴリを取得しDTOで返す")
        void returnsDto() {
            Category c = Category.createNew(CategoryName.of("文房具"));
            CategoryDTO dto = new CategoryDTO(CAT_UUID, "文房具");
            when(categoryService.getCategoryById(any())).thenReturn(c);
            when(assembler.toCategoryDto(c)).thenReturn(dto);

            assertSame(dto, interactor.getCategoryById(CAT_UUID));
        }
    }

    @Nested
    @DisplayName("existsProduct")
    class ExistsProduct {

        @Test
        @DisplayName("ProductName を生成して productService へ委譲する")
        void delegates() {
            interactor.existsProduct("蛍光ペン");
            verify(productService).existsProduct(ProductName.of("蛍光ペン"));
        }

        @Test
        @DisplayName("登録済みなら ExistsException を伝播する")
        void propagatesExists() {
            doThrow(new ExistsException("dup")).when(productService).existsProduct(any());
            assertThrows(ExistsException.class, () -> interactor.existsProduct("蛍光ペン"));
        }
    }

    @Nested
    @DisplayName("addProduct")
    class AddProduct {

        private ProductDTO input() {
            return new ProductDTO(null, "蛍光ペン", 130,
                    new CategoryDTO(CAT_UUID, "文房具"),
                    new StockDTO(null, 10));
        }

        @Test
        @DisplayName("カテゴリ取得→合成→重複チェック→登録→再取得→DTO返却")
        void success() {
            ProductDTO input = input();
            Category category = Category.createNew(CategoryName.of("文房具"));
            CategoryDTO categoryFromDb = new CategoryDTO(CAT_UUID, "文房具");
            Product toRegister = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("蛍光ペン"), ProductPrice.of(130));
            Product registered = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("蛍光ペン"), ProductPrice.of(130));
            ProductDTO resultDto = new ProductDTO("pid", "蛍光ペン", 130, categoryFromDb, new StockDTO("sid", 10));

            when(categoryService.getCategoryById(any())).thenReturn(category);
            when(assembler.toCategoryDto(category)).thenReturn(categoryFromDb);
            when(assembler.assembleDomain(input)).thenReturn(toRegister);
            when(productService.getProductByName(any())).thenReturn(registered);
            when(assembler.assembleDto(registered)).thenReturn(resultDto);

            ProductDTO result = interactor.addProduct(input);

            assertSame(resultDto, result);
            verify(productService).existsProduct(toRegister.getName());
            verify(productService).addProduct(toRegister);
        }

        @Test
        @DisplayName("DTO が null なら InvalidInputException")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> interactor.addProduct(null));
        }

        @Test
        @DisplayName("カテゴリが未設定なら InvalidInputException")
        void nullCategory() {
            ProductDTO input = new ProductDTO(null, "蛍光ペン", 130, null, new StockDTO(null, 10));
            assertThrows(InvalidInputException.class, () -> interactor.addProduct(input));
        }

        @Test
        @DisplayName("同名商品が存在すれば ExistsException（登録は行わない）")
        void duplicate_throwsExists() {
            ProductDTO input = input();
            Category category = Category.createNew(CategoryName.of("文房具"));
            CategoryDTO categoryFromDb = new CategoryDTO(CAT_UUID, "文房具");
            Product toRegister = Product.restoreSkeleton(
                    ProductId.createNew(), ProductName.of("蛍光ペン"), ProductPrice.of(130));

            when(categoryService.getCategoryById(any())).thenReturn(category);
            when(assembler.toCategoryDto(any())).thenReturn(categoryFromDb);
            when(assembler.assembleDomain(any())).thenReturn(toRegister);
            doThrow(new ExistsException("dup")).when(productService).existsProduct(any());

            assertThrows(ExistsException.class, () -> interactor.addProduct(input));
            verify(productService, never()).addProduct(any());
        }
    }
}