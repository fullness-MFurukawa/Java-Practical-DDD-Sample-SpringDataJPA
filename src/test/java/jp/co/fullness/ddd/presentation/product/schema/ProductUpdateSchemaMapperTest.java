package jp.co.fullness.ddd.presentation.product.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jp.co.fullness.ddd.application.dto.ProductDTO;

/**
 * {@link ProductUpdateSchemaMapper}（ProductUpdateSchema → ProductDTO）の単体テスト。
 *
 * <p>純粋な変換ロジックのため Spring / DB は不要。MapStruct が生成する実装
 * {@code ProductUpdateSchemaMapperImpl} を直接生成して検証する。</p>
 */
@DisplayName("ProductUpdateSchemaMapper: Schema → ProductDTO の変換")
class ProductUpdateSchemaMapperTest {

    /** MapStruct 生成の実装（componentModel=spring でも new で直接生成できる） */
    private final ProductUpdateSchemaMapper mapper = new ProductUpdateSchemaMapperImpl();

    @Test
    @DisplayName("スキーマを ProductDTO に変換する（id未設定・category未設定・stockをネスト）")
    void toDto() {
        var schema = new ProductUpdateSchema("筆ペン（極細）", 350, 20);

        ProductDTO dto = mapper.toDto(schema);

        // id はパス {id} から補完するため未設定、name/price は自動マッピング
        assertNull(dto.getId());
        assertEquals("筆ペン（極細）", dto.getName());
        assertEquals(350, dto.getPrice());

        // category は変更対象外のため未設定（null）
        assertNull(dto.getCategory());

        // stock: id は null、quantity は schema.stockQuantity
        assertNotNull(dto.getStock());
        assertNull(dto.getStock().getId());
        assertEquals(20, dto.getStock().getQuantity());
    }
}