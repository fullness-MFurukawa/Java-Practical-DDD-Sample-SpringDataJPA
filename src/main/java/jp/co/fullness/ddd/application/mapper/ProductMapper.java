package jp.co.fullness.ddd.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;

/**
 * {@link Product} エンティティと {@link ProductDTO} の相互変換を行うMapper。
 *
 * <p>商品のスケルトン（id, name, price）のみを扱う。カテゴリ・在庫は
 * {@link ProductDTOAssembler} が担当する。</p>
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends DomainBiMapper<ProductDTO, Product> {

    /**
     * ProductDTO から Product エンティティをスケルトンとして再構築する。
     * IDが未設定なら新規採番、設定済みなら復元する。
     *
     * @param dto ProductDTO
     * @return Product（カテゴリ・在庫を含まないスケルトン）
     * @throws InvalidInputException 必須項目が欠落している場合
     */
    @Override
    default Product toDomain(ProductDTO dto) {
        if (dto == null) {
            throw new InvalidInputException("ProductDTOがnullです。");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new InvalidInputException("商品名は必須です。");
        }
        if (dto.getPrice() == null) {
            throw new InvalidInputException("商品単価は必須です。");
        }
        return Product.restoreSkeleton(
                StringUtils.hasText(dto.getId()) ? ProductId.fromString(dto.getId()) : ProductId.createNew(),
                ProductName.of(dto.getName()),
                ProductPrice.of(dto.getPrice()));
    }

    /**
     * Product エンティティを ProductDTO（スケルトン）に変換する。
     * カテゴリ・在庫は含めない。
     *
     * @param domain Product
     * @return ProductDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    @Override
    default ProductDTO fromDomain(Product domain) {
        if (domain == null) {
            throw new InvalidInputException("Productがnullです。");
        }
        return new ProductDTO(
                domain.getProductId().value(),
                domain.getName().value(),
                domain.getPrice().value(),
                null, null);
    }
}
