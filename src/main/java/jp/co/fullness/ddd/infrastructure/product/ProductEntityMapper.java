package jp.co.fullness.ddd.infrastructure.product;

import org.mapstruct.Mapper;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;

/**
 * JPA の {@link ProductEntity} とエンティティ {@link Product}（骨格）を相互変換する Mapper。
 *
 * <p>{@code toDomain} はフラットな product カラムのみを使い、カテゴリ・在庫を伴わない
 * 骨格（{@code restoreSkeleton}）を返す。関連（{@code entity.getCategory()} /
 * {@code entity.getStock()}）はここでは扱わず、Assembler が別 Mapper で変換・合成する。</p>
 */
@Mapper(componentModel = "spring")
public interface ProductEntityMapper extends DomainBiMapper<ProductEntity, Product> {

    @Override
    default Product toDomain(ProductEntity entity) {
        if (entity == null) {
            throw new DomainException("商品情報が取得できません。");
        }

        String productUuid = entity.getProductUuid();
        String name = entity.getName();
        Integer price = entity.getPrice();

        if (productUuid == null || productUuid.isBlank()) {
            throw new DomainException("商品UUIDが不正です。");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("商品名が未設定です。");
        }
        if (price == null) {
            throw new DomainException("商品価格が未設定です。");
        }

        return Product.restoreSkeleton(
                ProductId.fromString(productUuid),
                ProductName.of(name),
                ProductPrice.of(price));
    }

    @Override
    default ProductEntity fromDomain(Product domain) {
        if (domain == null) {
            throw new DomainException("Product エンティティが null です。");
        }

        ProductEntity entity = new ProductEntity();
        entity.setProductUuid(domain.getProductId().value());
        entity.setName(domain.getName().value());
        entity.setPrice(domain.getPrice().value());
        // category / stock / id は Repository/Assembler 側で設定する
        return entity;
    }
}