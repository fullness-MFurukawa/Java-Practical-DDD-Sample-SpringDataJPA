package jp.co.fullness.ddd.infrastructure.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * product_category テーブル用の Spring Data JPA リポジトリ。
 *
 * <p>商品登録時に、ドメインの category UUID から実在するカテゴリエンティティ
 *（外部キー解決に使う）を引くために利用する。</p>
 */
public interface CategoryJpaRepository extends JpaRepository<ProductCategoryEntity, Integer> {

    /**
     * category_uuid でカテゴリを1件取得する。
     *
     * @param categoryUuid カテゴリの識別Id
     * @return 見つかれば {@link ProductCategoryEntity} を持つ Optional
     */
    Optional<ProductCategoryEntity> findByCategoryUuid(String categoryUuid);
}