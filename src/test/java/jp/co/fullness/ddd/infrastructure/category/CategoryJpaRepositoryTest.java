package jp.co.fullness.ddd.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link CategoryJpaRepository}（JPA 永続化層）の結合テスト（実 PostgreSQL に接続）。
 *
 * <p>Spring Data JPA の派生クエリ {@code findByCategoryUuid} と、継承した
 * {@code findAll} を実データに対して検証する。</p>
 *
 * <p><b>前提：</b>ローカル PostgreSQL が起動し、{@code restapi_exercise} に
 * サンプルデータ（カテゴリ 5 件）が投入済みであること。読み取りのみだが
 * {@code @Transactional} を付けておく。</p>
 *
 * <p>※ リポジトリ名を {@code ProductCategoryJpaRepository} のままにしている場合は、
 * この {@code @Autowired} の型・import・クラス名を読み替えること。</p>
 */
@SpringBootTest
@Transactional
@DisplayName("CategoryJpaRepository 結合テスト（ローカル PostgreSQL / サンプルデータ前提）")
class CategoryJpaRepositoryTest {

    @Autowired
    private CategoryJpaRepository repository;

    @Nested
    @DisplayName("findByCategoryUuid")
    class FindByCategoryUuid {

        @Test
        @DisplayName("実在する category_uuid でカテゴリを取得できる")
        void existing() {
            // サンプルデータから実在する1件を取り、その UUID で引き直す
            List<CategoryEntity> all = repository.findAll();
            assertFalse(all.isEmpty(), "サンプルのカテゴリが存在すること");
            CategoryEntity sample = all.get(0);

            Optional<CategoryEntity> found = repository.findByCategoryUuid(sample.getCategoryUuid());

            assertTrue(found.isPresent());
            assertEquals(sample.getId(), found.get().getId());
            assertEquals(sample.getCategoryUuid(), found.get().getCategoryUuid());
            assertEquals(sample.getName(), found.get().getName());
        }

        @Test
        @DisplayName("存在しない category_uuid なら空の Optional")
        void missing() {
            assertTrue(repository.findByCategoryUuid(UUID.randomUUID().toString()).isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("サンプルデータのカテゴリ『文房具』が取得できる")
        void containsSampleCategory() {
            boolean hasStationery = repository.findAll().stream()
                    .anyMatch(c -> "文房具".equals(c.getName()));

            assertTrue(hasStationery, "サンプルデータに『文房具』カテゴリが存在すること");
        }
    }
}