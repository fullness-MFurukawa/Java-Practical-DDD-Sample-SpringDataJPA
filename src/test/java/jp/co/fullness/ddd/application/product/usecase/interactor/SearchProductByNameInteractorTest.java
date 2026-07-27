package jp.co.fullness.ddd.application.product.usecase.interactor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;

/**
 * {@link SearchProductByNameInteractor} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>ProductService / Assembler をモック化し、委譲と DTO 変換、未存在時の
 * NotFoundException 伝播を検証する。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchProductByNameInteractor: 商品名検索ユースケース")
class SearchProductByNameInteractorTest {

    @Mock
    private ProductService service;
    @Mock
    private ProductDTOAssembler assembler;

    @InjectMocks
    private SearchProductByNameInteractor interactor;

    @Test
    @DisplayName("見つかれば商品をDTOに変換して返す")
    void found() {
        Product product = Product.restoreSkeleton(
                ProductId.createNew(), ProductName.of("蛍光ペン"), ProductPrice.of(130));
        ProductDTO dto = new ProductDTO("pid", "蛍光ペン", 130, null, null);

        when(service.getProductByName(any())).thenReturn(product);
        when(assembler.assembleDto(product)).thenReturn(dto);

        assertSame(dto, interactor.search("蛍光ペン"));
    }

    @Test
    @DisplayName("見つからなければ NotFoundException を伝播する")
    void notFound_propagates() {
        when(service.getProductByName(any())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> interactor.search("存在しない商品"));
    }
}
