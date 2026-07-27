package jp.co.fullness.ddd.infrastructure.product;

import org.springframework.stereotype.Component;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.infrastructure.category.CategoryEntityMapper;
import jp.co.fullness.ddd.infrastructure.stock.ProductStockEntity;
import jp.co.fullness.ddd.infrastructure.stock.StockEntityMapper;

/**
 * Product 集約の「合成（Entity → 集約）」および「分解（集約 → Entity）」を担うアセンブラ。
 *
 * <p>責務は<b>型変換と合成/分解のみ</b>で、永続化（save）は Repository が担う。
 * JPA では1つの {@link ProductEntity} が関連（category / stock）をエンティティグラフとして
 * 保持するため、合成は entity 1つを受け取れば足りる。骨格（{@code restoreSkeleton}）に
 * カテゴリ・在庫を {@code attach} して集約を組み立てる。</p>
 */
@Component
public class ProductAssembler {

    private final ProductEntityMapper productEntityMapper;
    private final CategoryEntityMapper categoryEntityMapper;
    private final StockEntityMapper stockEntityMapper;

    public ProductAssembler(ProductEntityMapper productEntityMapper,
                            CategoryEntityMapper categoryEntityMapper,
                            StockEntityMapper stockEntityMapper) {
        this.productEntityMapper = productEntityMapper;
        this.categoryEntityMapper = categoryEntityMapper;
        this.stockEntityMapper = stockEntityMapper;
    }

    // ----------------------------------------------------------------------
    // 合成（Entity → 集約）
    // ----------------------------------------------------------------------

    /**
     * {@link ProductEntity}（関連を含むグラフ）から完全な {@link Product} を合成する。
     *
     * @param entity 商品エンティティ（category / stock を含む）
     * @return 合成済みの Product 集約
     * @throws DomainException 必須の関連が欠落している場合など
     */
    public Product assemble(ProductEntity entity) {
        if (entity == null) {
            throw new DomainException("ProductEntity が null です。");
        }
        if (entity.getCategory() == null) {
            throw new DomainException("商品にカテゴリが紐づいていません。");
        }
        if (entity.getStock() == null) {
            throw new DomainException("商品に在庫が紐づいていません。");
        }

        var product = productEntityMapper.toDomain(entity);          // skeleton
        product.attachCategory(categoryEntityMapper.toDomain(entity.getCategory()));
        product.attachStock(stockEntityMapper.toDomain(entity.getStock()));
        return product;
    }

    // ----------------------------------------------------------------------
    // 分解（集約 → Entity）
    // ----------------------------------------------------------------------

    /**
     * 集約から ProductEntity を作る（INSERT 用）。
     * 関連 category / stock はここでは設定しない（Repository が設定・cascade する）。
     */
    public ProductEntity toProductEntity(Product product) {
        if (product == null) {
            throw new DomainException("Product が null です。");
        }
        return productEntityMapper.fromDomain(product);
    }

    /**
     * 集約から ProductStockEntity を作る（INSERT 用）。
     * 関連 product（外部キー）はここでは設定しない（Repository が {@code assignStock} で設定する）。
     */
    public ProductStockEntity toStockEntity(Product product) {
        if (product == null) {
            throw new DomainException("Product が null です。");
        }
        var stock = product.getStock();
        if (stock == null) {
            throw new DomainException("Product に Stock が設定されていません。");
        }
        return stockEntityMapper.fromDomain(stock);
    }

    /**
     * 集約から Category の UUID（文字列）を取り出す。Repository で実在カテゴリを引くために利用する。
     */
    public String extractCategoryUuid(Product product) {
        if (product == null) {
            throw new DomainException("Product が null です。");
        }
        var category = product.getCategory();
        if (category == null) {
            throw new DomainException("Product に Category が設定されていません。");
        }
        return category.getCategoryId().value();
    }
}