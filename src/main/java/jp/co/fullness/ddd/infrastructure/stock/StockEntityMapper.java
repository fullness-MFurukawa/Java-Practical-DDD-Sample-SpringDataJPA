package jp.co.fullness.ddd.infrastructure.stock;

import org.mapstruct.Mapper;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockId;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * JPA の {@link ProductStockEntity} とエンティティ {@link Stock} を相互変換する Mapper。
 *
 * <p>腐敗防止層（ACL）として、永続化構造（Entity）とドメイン構造（Stock）の依存を絶つ。</p>
 */
@Mapper(componentModel = "spring")
public interface StockEntityMapper extends DomainBiMapper<ProductStockEntity, Stock> {

    @Override
    default Stock toDomain(ProductStockEntity entity) {
        if (entity == null) {
            throw new DomainException("在庫情報が取得できません。");
        }

        String stockUuid = entity.getStockUuid();
        Integer quantity = entity.getStock();

        if (stockUuid == null || stockUuid.isBlank()) {
            throw new DomainException("在庫UUIDが不正です。");
        }
        if (quantity == null) {
            throw new DomainException("在庫数が未設定です。");
        }

        return Stock.restore(
                StockId.fromString(stockUuid),
                StockQuantity.of(quantity));
    }

    @Override
    default ProductStockEntity fromDomain(Stock domain) {
        if (domain == null) {
            throw new DomainException("Stock エンティティが null です。");
        }

        ProductStockEntity entity = new ProductStockEntity();
        entity.setStockUuid(domain.getStockId().value());
        entity.setStock(domain.getQuantity().value());
        // 関連 product（外部キー）は Repository/Assembler 側で設定する
        return entity;
    }
}