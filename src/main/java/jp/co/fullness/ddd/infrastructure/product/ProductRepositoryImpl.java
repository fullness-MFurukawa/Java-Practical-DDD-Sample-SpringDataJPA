package jp.co.fullness.ddd.infrastructure.product;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductRepository;
import jp.co.fullness.ddd.infrastructure.category.ProductCategoryEntity;
import jp.co.fullness.ddd.infrastructure.category.CategoryJpaRepository;
import jp.co.fullness.ddd.infrastructure.exception.InternalException;
import jp.co.fullness.ddd.infrastructure.stock.ProductStockEntity;

/**
 * {@link ProductRepository} の Spring Data JPA による実装。
 *
 * <p>SQL 実行は {@link ProductJpaRepository} / {@link CategoryJpaRepository} へ、
 * Entity ↔ 集約 の変換は {@link ProductAssembler} へ委譲する。</p>
 *
 * <p>JPA の例外は Spring の {@link org.springframework.dao.DataAccessException} に
 * 翻訳されるため、そこを捕捉する。</p>
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductAssembler assembler;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository,
                                 CategoryJpaRepository categoryJpaRepository,
                                 ProductAssembler assembler) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.assembler = assembler;
    }

    @Override
    @Transactional
    public void create(Product product) {
        if (product == null) {
            throw new DomainException("商品は必須です。");
        }
        try {
            // カテゴリUUID → 実在するカテゴリエンティティ（外部キー参照）を解決
            String categoryUuid = assembler.extractCategoryUuid(product);
            ProductCategoryEntity categoryEntity = categoryJpaRepository.findByCategoryUuid(categoryUuid)
                    .orElseThrow(() -> new DomainException("指定された商品カテゴリが存在しません。"));

            ProductEntity pe = assembler.toProductEntity(product);
            pe.setCategory(categoryEntity);

            ProductStockEntity se = assembler.toStockEntity(product);
            // 双方向 OneToOne の所有側（product_stock.product_id）も設定する
            pe.assignStock(se);

            // cascade = ALL により、product と stock が1トランザクションで保存される
            productJpaRepository.save(pe);

        } catch (DomainException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new InternalException("商品登録中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品登録処理中に予期しないエラーが発生しました。", ex);
        }
    }

    @Override
    @Transactional
    public void update(Product product) {
        if (product == null) {
            throw new DomainException("商品は必須です。");
        }
        try {
            // 変更対象の管理下(managed)エンティティを product_uuid で取得する
            ProductEntity managed = productJpaRepository
                    .findByProductUuid(product.getProductId().value())
                    .orElseThrow(() -> new InternalException("更新対象の商品が見つかりませんでした。"));

            // ドメインの変更後の状態を管理下エンティティに反映する
            // ※カテゴリは「商品を変更する」ユースケースの変更対象外のため触らない
            managed.setName(product.getName().value());
            managed.setPrice(product.getPrice().value());
            // 在庫は同一の子エンティティ（同じ stock_uuid）の数量のみを変更する
            managed.getStock().setStock(product.getStock().getQuantity().value());

            // managed エンティティのためダーティチェックで UPDATE されるが、明示的に save して意図を示す
            productJpaRepository.save(managed);

        } catch (DomainException ex) {
            throw ex;          // ドメイン例外はそのまま伝播させる
        } catch (InternalException ex) {
            throw ex;          // 自前で投げた InternalException を generic catch で二重ラップしない
        } catch (DataAccessException ex) {
            throw new InternalException("商品変更中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品変更処理中に予期しないエラーが発生しました。", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(ProductName productName) {
        if (productName == null) {
            throw new DomainException("商品名は必須です。");
        }
        try {
            return productJpaRepository.existsByName(productName.value());
        } catch (DataAccessException ex) {
            throw new InternalException("商品名の存在確認中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品名の存在確認処理中に予期しないエラーが発生しました。", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(ProductId productId) {
        if (productId == null) {
            throw new DomainException("商品Idは必須です。");
        }
        try {
            // EAGER 関連なのでトランザクション内でグラフごと取得され、assemble まで安全に行える
            return productJpaRepository.findByProductUuid(productId.value())
                    .map(assembler::assemble);
        } catch (DomainException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new InternalException("商品情報の取得中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品情報の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByName(ProductName productName) {
        if (productName == null) {
            throw new DomainException("商品名は必須です。");
        }
        try {
            return productJpaRepository.findByName(productName.value())
                    .map(assembler::assemble);
        } catch (DomainException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new InternalException("商品名による検索中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("商品名による検索処理中に予期しないエラーが発生しました。", ex);
        }
    }
}