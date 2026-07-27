package jp.co.fullness.ddd.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;

/**
 * {@link CategoryRepositoryImpl}（JPA 実装）の結合テスト（ローカル PostgreSQL / サンプルデータ前提）。
 *
 * <p>ドメイン向けの {@code CategoryRepository} として findById / findAll を検証する。
 * category_uuid はランダム生成のため、findAll から実データを取得して findById を引き直す。</p>
 */
@SpringBootTest
@DisplayName("CategoryRepositoryImpl（JPA）結合テスト")
class CategoryRepositoryImplTest {

    @Autowired
    private CategoryRepositoryImpl repository;

    @Test
    @DisplayName("findById(): カテゴリが存在すれば取得できる")
    void findById_found() {
        Category stationery = repository.findAll().stream()
                .filter(c -> "文房具".equals(c.getName().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("サンプルの『文房具』カテゴリが見つからない"));

        Optional<Category> found = repository.findById(stationery.getCategoryId());

        assertTrue(found.isPresent());
        assertEquals("文房具", found.get().getName().value());
        assertEquals(stationery.getCategoryId().value(), found.get().getCategoryId().value());
    }

    @Test
    @DisplayName("findById(): 存在しないカテゴリIdなら empty を返す")
    void findById_notFound() {
        Optional<Category> found = repository.findById(
                CategoryId.fromString(UUID.randomUUID().toString()));

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findAll(): サンプルのカテゴリ『文房具』が取得できる")
    void findAll_returnsSamples() {
        boolean hasStationery = repository.findAll().stream()
                .anyMatch(c -> "文房具".equals(c.getName().value()));

        assertTrue(hasStationery);
    }
}