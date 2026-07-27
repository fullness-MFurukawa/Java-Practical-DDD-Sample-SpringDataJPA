package jp.co.fullness.ddd.application.product.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.product.ProductRepository;

/**
 * {@link ProductServiceImpl} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>{@link ProductRepository} をモック化し、委譲・存在チェック（ExistsException）・
 * 未存在（NotFoundException）の変換だけを検証する。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl: 商品サービス")
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product sampleProduct() {
        return Product.restoreSkeleton(
                ProductId.createNew(), ProductName.of("蛍光ペン"), ProductPrice.of(130));
    }

    @Nested
    @DisplayName("existsProduct")
    class ExistsProduct {

        @Test
        @DisplayName("未登録なら例外を投げない")
        void notExists() {
            ProductName name = ProductName.of("蛍光ペン");
            when(repository.existsByName(name)).thenReturn(false);

            assertDoesNotThrow(() -> service.existsProduct(name));
        }

        @Test
        @DisplayName("登録済みなら ExistsException")
        void exists() {
            ProductName name = ProductName.of("蛍光ペン");
            when(repository.existsByName(name)).thenReturn(true);

            assertThrows(ExistsException.class, () -> service.existsProduct(name));
        }
    }

    @Nested
    @DisplayName("getProductById")
    class GetById {

        @Test
        @DisplayName("見つかれば Product を返す")
        void found() {
            Product p = sampleProduct();
            ProductId id = p.getProductId();
            when(repository.findById(id)).thenReturn(Optional.of(p));

            assertSame(p, service.getProductById(id));
        }

        @Test
        @DisplayName("見つからなければ NotFoundException")
        void notFound() {
            ProductId id = ProductId.createNew();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getProductById(id));
        }
    }

    @Nested
    @DisplayName("getProductByName")
    class GetByName {

        @Test
        @DisplayName("見つかれば Product を返す")
        void found() {
            Product p = sampleProduct();
            ProductName name = ProductName.of("蛍光ペン");
            when(repository.findByName(name)).thenReturn(Optional.of(p));

            assertSame(p, service.getProductByName(name));
        }

        @Test
        @DisplayName("見つからなければ NotFoundException")
        void notFound() {
            ProductName name = ProductName.of("存在しない商品");
            when(repository.findByName(name)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getProductByName(name));
        }
    }

    @Nested
    @DisplayName("addProduct")
    class AddProduct {

        @Test
        @DisplayName("repository.create に委譲する")
        void delegates() {
            Product p = sampleProduct();

            service.addProduct(p);

            verify(repository).create(p);
        }
    }
}