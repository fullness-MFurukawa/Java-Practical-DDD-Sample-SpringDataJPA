package jp.co.fullness.ddd.infrastructure.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * product テーブル用の Spring Data JPA リポジトリ。
 *
 * <p>jOOQ 版の DSLContext、MyBatis 版の SQL マッパーに相当する「SQL 実行」の担い手。
 * 返却型は JPA エンティティであり、ドメインへの変換は {@code ProductRepositoryImpl} が
 * {@code ProductAssembler} を通して行う。</p>
 *
 * <p>{@code findBy...} は関連（category / stock）を EAGER で辿るため、1件取得で
 * 集約全体のエンティティグラフが得られる。</p>
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {

    /** 商品名の存在確認 */
    boolean existsByName(String name);

    /** product_uuid で1件取得 */
    Optional<ProductEntity> findByProductUuid(String productUuid);

    /** 商品名で1件取得 */
    Optional<ProductEntity> findByName(String name);
}