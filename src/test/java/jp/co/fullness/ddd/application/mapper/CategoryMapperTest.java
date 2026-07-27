package jp.co.fullness.ddd.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.category.CategoryName;

/**
 * {@link CategoryMapper}（CategoryDTO ⇔ Category）の単体テスト（DI 経由）。
 */
@SpringBootTest
@DisplayName("CategoryMapper: CategoryDTO ⇔ Category の相互変換")
class CategoryMapperTest {

    @Autowired
    private CategoryMapper mapper;

    private static final String UUID_STR = "11111111-1111-1111-1111-111111111111";

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("id 指定ありなら restore（そのIDで復元）")
        void withId_restore() {
            Category c = mapper.toDomain(new CategoryDTO(UUID_STR, "文房具"));
            assertEquals(UUID_STR, c.getCategoryId().value());
            assertEquals("文房具", c.getName().value());
        }

        @Test
        @DisplayName("id 未指定なら createNew（新規採番・IDが振られる）")
        void withoutId_createNew() {
            Category c = mapper.toDomain(new CategoryDTO(null, "文房具"));
            assertNotNull(c.getCategoryId().value());
            assertEquals("文房具", c.getName().value());
        }

        @Test
        @DisplayName("DTO が null なら例外")
        void nullDto() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(null));
        }

        @Test
        @DisplayName("name が空なら例外")
        void blankName() {
            assertThrows(InvalidInputException.class, () -> mapper.toDomain(new CategoryDTO(UUID_STR, "  ")));
        }
    }

    @Nested
    @DisplayName("fromDomain")
    class FromDomain {

        @Test
        @DisplayName("Category を CategoryDTO に変換できる")
        void valid() {
            Category c = Category.restore(CategoryId.fromString(UUID_STR), CategoryName.of("文房具"));

            CategoryDTO dto = mapper.fromDomain(c);

            assertEquals(UUID_STR, dto.getId());
            assertEquals("文房具", dto.getName());
        }

        @Test
        @DisplayName("null なら例外")
        void nullDomain() {
            assertThrows(InvalidInputException.class, () -> mapper.fromDomain(null));
        }
    }
}