package jp.co.fullness.ddd.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;

/**
 * {@link ProductMapper}（ProductDTO ⇔ Product 骨格）の単体テスト（DI 経由）。
 */
@SpringBootTest
@DisplayName("ProductMapper: ProductDTO ⇔ Product（骨格）の相互変換")
class ProductMapperTest {

    @Autowired
    private ProductMapper mapper;

    private static final String UUID_STR = "33333333-3333-3333-3333-333333333333";

    private ProductDTO dto(String id, String name, Integer price) {
        return new ProductDTO(id, name, price, null, null);
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("id 指定ありなら restoreSkeleton（そのIDで復元・カテゴリ/在庫は null）")
        void withId() {
            Product p = mapper.toDomain(dto(UUID_STR, "蛍光ペン", 130));
            assertEquals(UUID_STR, p.getProductId().value());
            assertEquals("蛍光ペン", p.getName().value());
            assertEquals(130, p.getPrice().value().intValue());
            assertNull(p.getCategory());
            assertNull(p.getStock());
        }

        @Test
        @DisplayName("id 未指定なら新規採番される")
        void withoutId() {
            Product p = mapper.toDomain(dto(null, "蛍光ペン", 130));
            assertNotNull(p.getProductId().value());
            assertEquals("蛍光ペン", p.getName().value());
            assertEquals(130, p.getPrice().value().intValue());
        }

        @Test
        @DisplayName("DTO が null なら例外")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("name が空なら例外")
        void blankName() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(dto(UUID_STR, "  ", 130)));
        }

        @Test
        @DisplayName("price が null なら例外")
        void nullPrice() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(dto(UUID_STR, "蛍光ペン", null)));
        }
    }

    @Nested
    @DisplayName("fromDomain")
    class FromDomain {

        @Test
        @DisplayName("Product を ProductDTO（骨格）に変換できる（カテゴリ/在庫は null）")
        void valid() {
            Product p = Product.restoreSkeleton(
                    ProductId.fromString(UUID_STR),
                    ProductName.of("蛍光ペン"),
                    ProductPrice.of(130));

            ProductDTO result = mapper.fromDomain(p);

            assertEquals(UUID_STR, result.getId());
            assertEquals("蛍光ペン", result.getName());
            assertEquals(130, result.getPrice().intValue());
            assertNull(result.getCategory());
            assertNull(result.getStock());
        }

        @Test
        @DisplayName("null なら例外")
        void nullDomain() {
            assertThrows(InvalidInputException.class, () -> mapper.fromDomain(null));
        }
    }
}