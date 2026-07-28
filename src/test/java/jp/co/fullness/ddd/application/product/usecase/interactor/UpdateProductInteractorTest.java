package jp.co.fullness.ddd.application.product.usecase.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link UpdateProductInteractor} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>{@link ProductService} と {@link ProductDTOAssembler} をモック化し、
 * 取得（GET）と更新（PUT）の処理フロー・入力ガード・重複（ExistsException）・
 * 未存在（NotFoundException）を検証する。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductInteractor: 商品を変更するユースケース")
class UpdateProductInteractorTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductDTOAssembler assembler;

    @InjectMocks
    private UpdateProductInteractor interactor;

    /** 在庫を attach 済みの変更対象商品を生成する（changeStock が動く状態） */
    private Product existingProduct(ProductId id, String name, int price, int quantity) {
        Product p = Product.restoreSkeleton(id, ProductName.of(name), ProductPrice.of(price));
        p.attachStock(Stock.createNew(StockQuantity.of(quantity)));
        return p;
    }

    private ProductDTO inputDto(String id, String name, int price, int quantity) {
        return new ProductDTO(id, name, price,
                new CategoryDTO("cid", "文房具"), new StockDTO("sid", quantity));
    }

    @Nested
    @DisplayName("getProduct（編集用の取得）")
    class GetProduct {
        @Test
        @DisplayName("見つかれば DTO を返す")
        void found() {
            ProductId id = ProductId.createNew();
            Product p = existingProduct(id, "蛍光ペン", 130, 10);
            ProductDTO dto = inputDto(id.value(), "蛍光ペン", 130, 10);

            when(productService.getProductById(any(ProductId.class))).thenReturn(p);
            when(assembler.assembleDto(p)).thenReturn(dto);

            assertSame(dto, interactor.getProduct(id.value()));
        }

        @Test
        @DisplayName("IDが空なら InvalidInputException")
        void blankId() {
            assertThrows(InvalidInputException.class, () -> interactor.getProduct("  "));
        }

        @Test
        @DisplayName("該当なしは NotFoundException を伝播する")
        void notFound() {
            when(productService.getProductById(any(ProductId.class)))
                    .thenThrow(new NotFoundException("なし"));
            assertThrows(NotFoundException.class,
                    () -> interactor.getProduct(ProductId.createNew().value()));
        }
    }

    @Nested
    @DisplayName("updateProduct（変更の反映）")
    class UpdateProduct {
        @Test
        @DisplayName("正常系：名称・単価・在庫数を変更し、変更後DTOを返す")
        void success() {
            ProductId id = ProductId.createNew();
            Product current = existingProduct(id, "旧商品", 200, 5);
            ProductDTO input = inputDto(id.value(), "新商品", 750, 42);
            ProductDTO expected = inputDto(id.value(), "新商品", 750, 42);

            // load と re-fetch の両方で current を返す
            when(productService.getProductById(any(ProductId.class))).thenReturn(current);
            when(assembler.assembleDto(current)).thenReturn(expected);

            ProductDTO result = interactor.updateProduct(input);

            // 返却は変換結果そのもの
            assertSame(expected, result);
            // 集約に変更が適用されていること
            assertEquals("新商品", current.getName().value());
            assertEquals(750, current.getPrice().value().intValue());
            assertEquals(42, current.getStock().getQuantity().value().intValue());
            // 自分自身を除く重複チェックと更新委譲が呼ばれていること
            verify(productService).existsProductExcept(ProductName.of("新商品"), id);
            verify(productService).updateProduct(current);
        }

        @Test
        @DisplayName("DTOがnullなら InvalidInputException")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> interactor.updateProduct(null));
        }

        @Test
        @DisplayName("IDが空なら InvalidInputException")
        void blankId() {
            ProductDTO input = inputDto("  ", "新商品", 750, 42);
            assertThrows(InvalidInputException.class, () -> interactor.updateProduct(input));
        }

        @Test
        @DisplayName("在庫（StockDTO）がnullなら InvalidInputException")
        void nullStock() {
            ProductDTO input = new ProductDTO(ProductId.createNew().value(), "新商品", 750,
                    new CategoryDTO("cid", "文房具"), null);
            assertThrows(InvalidInputException.class, () -> interactor.updateProduct(input));
        }

        @Test
        @DisplayName("他商品が同名を使用中なら ExistsException（更新は呼ばれない）")
        void duplicateName() {
            ProductId id = ProductId.createNew();
            Product current = existingProduct(id, "旧商品", 200, 5);
            ProductDTO input = inputDto(id.value(), "重複名", 750, 42);

            when(productService.getProductById(any(ProductId.class))).thenReturn(current);
            doThrow(new ExistsException("同名商品が存在します"))
                    .when(productService).existsProductExcept(any(), any());

            assertThrows(ExistsException.class, () -> interactor.updateProduct(input));
            verify(productService, never()).updateProduct(any());
        }

        @Test
        @DisplayName("対象が存在しなければ NotFoundException（更新は呼ばれない）")
        void notFound() {
            ProductDTO input = inputDto(ProductId.createNew().value(), "新商品", 750, 42);

            when(productService.getProductById(any(ProductId.class)))
                    .thenThrow(new NotFoundException("商品が存在しません"));

            assertThrows(NotFoundException.class, () -> interactor.updateProduct(input));
            verify(productService, never()).updateProduct(any());
        }
    }
}
