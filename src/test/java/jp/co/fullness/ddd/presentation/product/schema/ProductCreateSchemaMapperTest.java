package jp.co.fullness.ddd.presentation.product.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.application.dto.ProductDTO;

/**
 * {@link ProductCreateSchemaMapper}（ProductCreateSchema → ProductDTO）の単体テスト。
 *
 * <p>純粋な変換ロジックのため Spring / DB は不要。MapStruct が生成する実装
 * {@code ProductCreateSchemaMapperImpl} を直接生成して検証する。</p>
 */
@DisplayName("ProductCreateSchemaMapper: Schema → ProductDTO の変換")
class ProductCreateSchemaMapperTest {

    /** MapStruct 生成の実装（componentModel=spring でも new で直接生成できる） */
    private final ProductCreateSchemaMapper mapper = new ProductCreateSchemaMapperImpl();

    private static final String CATEGORY_UUID = "2d8e2b0d-49ef-4b36-a4f3-1c6a2e0b84c4";

    @Test
    @DisplayName("スキーマを ProductDTO に変換する（id未設定・category/stockをネスト）")
    void toDto() {
        var schema = new ProductCreateSchema("筆ペン", 300, CATEGORY_UUID, 10);

        ProductDTO dto = mapper.toDto(schema);

        // id は新規採番のため未設定、name/price は自動マッピング
        assertNull(dto.getId());
        assertEquals("筆ペン", dto.getName());
        assertEquals(300, dto.getPrice());

        // category: id は schema.categoryId、name は null（登録時にDBの正で上書きされる）
        assertNotNull(dto.getCategory());
        assertEquals(CATEGORY_UUID, dto.getCategory().getId());
        assertNull(dto.getCategory().getName());

        // stock: id は null、quantity は schema.stockQuantity
        assertNotNull(dto.getStock());
        assertNull(dto.getStock().getId());
        assertEquals(10, dto.getStock().getQuantity());
    }
}
