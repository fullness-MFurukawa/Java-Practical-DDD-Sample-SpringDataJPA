package jp.co.fullness.ddd.infrastructure.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ddd.infrastructure.category.ProductCategoryEntity;
import jp.co.fullness.ddd.infrastructure.stock.ProductStockEntity;

/**
 * {@link ProductJpaRepository}（JPA 永続化層）の結合テスト（実 PostgreSQL に接続）。
 *
 * <p>Repository アダプタを通さず、JPA エンティティのレベルで次を直接検証する。</p>
 * <ul>
 *   <li>{@code findByProductUuid} / {@code findByName} が関連（category / stock）を
 *       グラフごと取得すること</li>
 *   <li>{@code save} が {@code cascade = ALL} により product と stock を一括保存すること</li>
 *   <li>{@code existsByName} の単純クエリ</li>
 * </ul>
 *
 * <p><b>前提：</b>ローカル PostgreSQL が起動し、{@code restapi_exercise} に
 * サンプルデータが投入済みであること。{@code @Transactional} で各テストは自動ロールバックされる。</p>
 */
@SpringBootTest
@Transactional
@DisplayName("ProductJpaRepository（JPA 永続化層）結合テスト（ローカル PostgreSQL / サンプルデータ前提）")
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    private static final String EXISTING_NAME = "油性ボールペン";
    private static final String MISSING_NAME = "存在しない商品ZZZ";

    @Nested
    @DisplayName("existsByName")
    class ExistsByName {

        @Test
        @DisplayName("存在する商品名なら true")
        void exists_true() {
            assertTrue(productJpaRepository.existsByName(EXISTING_NAME));
        }

        @Test
        @DisplayName("存在しない商品名なら false")
        void exists_false() {
            assertFalse(productJpaRepository.existsByName(MISSING_NAME));
        }
    }

    @Nested
    @DisplayName("findByName / findByProductUuid（関連グラフの取得）")
    class Find {

        @Test
        @DisplayName("findByName でカテゴリ・在庫までグラフ取得できる")
        void findByName_withGraph() {
            ProductEntity entity = productJpaRepository.findByName(EXISTING_NAME).orElseThrow();

            assertEquals(EXISTING_NAME, entity.getName());
            assertEquals(120, entity.getPrice().intValue());
            assertNotNull(entity.getCategory(), "category が取得されること");
            assertEquals("文房具", entity.getCategory().getName());
            assertNotNull(entity.getStock(), "stock が取得されること");
            assertEquals(80, entity.getStock().getStock().intValue());
        }

        @Test
        @DisplayName("findByProductUuid は findByName と同じ商品を返す")
        void findByProductUuid_matches() {
            ProductEntity byName = productJpaRepository.findByName(EXISTING_NAME).orElseThrow();

            Optional<ProductEntity> byUuid = productJpaRepository.findByProductUuid(byName.getProductUuid());
            assertTrue(byUuid.isPresent());
            assertEquals(byName.getProductUuid(), byUuid.get().getProductUuid());
            assertEquals(byName.getStock().getStock(), byUuid.get().getStock().getStock());
        }
    }

    @Nested
    @DisplayName("save（cascade で product＋stock を一括保存）")
    class Save {

        @Test
        @DisplayName("新規商品を在庫つきで保存すると、cascade で在庫も永続化される")
        void save_cascadesStock() {
            // 既存商品から実在するカテゴリエンティティを借りる（外部キー）
            ProductCategoryEntity category =
                    productJpaRepository.findByName(EXISTING_NAME).orElseThrow().getCategory();

            ProductEntity pe = new ProductEntity();
            pe.setProductUuid(UUID.randomUUID().toString());
            pe.setName("JPA結合テスト商品");
            pe.setPrice(500);
            pe.setCategory(category);

            ProductStockEntity se = new ProductStockEntity();
            se.setStockUuid(UUID.randomUUID().toString());
            se.setStock(15);
            pe.assignStock(se);   // 双方向 OneToOne の所有側（product_id）も設定

            productJpaRepository.save(pe);   // cascade=ALL で stock も保存される

            // 取り直して、cascade で在庫が永続化されていることを確認
            ProductEntity found = productJpaRepository.findByProductUuid(pe.getProductUuid()).orElseThrow();
            assertEquals("JPA結合テスト商品", found.getName());
            assertEquals(500, found.getPrice().intValue());
            assertNotNull(found.getStock(), "cascade で在庫が保存されていること");
            assertEquals(15, found.getStock().getStock().intValue());
            assertEquals(category.getCategoryUuid(), found.getCategory().getCategoryUuid());
        }
    }
}