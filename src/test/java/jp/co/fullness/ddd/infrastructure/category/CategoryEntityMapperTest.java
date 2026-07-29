package jp.co.fullness.ddd.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.category.Category;

/**
 * {@link CategoryEntityMapper} の単体テスト（Spring コンテナ経由）。
 *
 * <p>DI で注入される MapStruct 実装 Bean を使い、JPA エンティティ →
 * ドメイン {@link Category} への変換を検証する。</p>
 */
@SpringBootTest
@DisplayName("CategoryEntityMapper: JPA Entity → Category の変換（DI 経由）")
class CategoryEntityMapperTest {

    @Autowired
    private CategoryEntityMapper mapper;

    /** ProductCategoryEntity は同一パッケージなので import 不要 */
    private CategoryEntity entity(String categoryUuid, String name) {
        CategoryEntity e = new CategoryEntity();
        e.setCategoryUuid(categoryUuid);
        e.setName(name);
        return e;
    }

    @Nested
    @DisplayName("正常系")
    class Success {

        @Test
        @DisplayName("有効な Entity を Category に変換できる")
        void toDomain_valid() {
            String uuid = "11111111-1111-1111-1111-111111111111";

            Category category = mapper.toDomain(entity(uuid, "文房具"));

            assertEquals(uuid, category.getCategoryId().value());
            assertEquals("文房具", category.getName().value());
        }
    }

    @Nested
    @DisplayName("異常系（DomainException を送出する）")
    class Failure {

        @Test
        @DisplayName("Entity が null なら例外")
        void toDomain_nullEntity() {
            assertThrows(DomainException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("category_uuid が空白なら例外")
        void toDomain_blankUuid() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity("   ", "文房具")));
        }

        @Test
        @DisplayName("name が空白なら例外")
        void toDomain_blankName() {
            String uuid = "11111111-1111-1111-1111-111111111111";
            assertThrows(DomainException.class, () -> mapper.toDomain(entity(uuid, "   ")));
        }

        @Test
        @DisplayName("category_uuid が UUID 形式でないなら例外（VO のバリデーション）")
        void toDomain_invalidUuidFormat() {
            assertThrows(DomainException.class, () -> mapper.toDomain(entity("not-a-uuid", "文房具")));
        }
    }
}
