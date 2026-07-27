package jp.co.fullness.ddd.application.mapper;

import org.springframework.stereotype.Component;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.stock.Stock;

import lombok.RequiredArgsConstructor;

/**
 * アプリケーション層のアセンブラ。
 *
 * <p>DTO群（ProductDTO / CategoryDTO / StockDTO）とドメイン集約 {@link Product} の
 * 合成／分解を、複数のMapperを統括して集約単位で行う。</p>
 */
@Component
@RequiredArgsConstructor
public class ProductDTOAssembler {

    private final DomainBiMapper<ProductDTO, Product> productMapper;
    private final DomainBiMapper<CategoryDTO, Category> categoryMapper;
    private final DomainBiMapper<StockDTO, Stock> stockMapper;

    /**
     * DTO群からドメイン集約 {@link Product} を合成する。
     *
     * @param dto 入力DTO
     * @return 合成済み {@link Product}
     * @throws InvalidInputException 必須DTO欠落など不整合
     * @throws DomainException       値オブジェクトの検証失敗など
     */
    public Product assembleDomain(ProductDTO dto) {
        if (dto == null) {
            throw new InvalidInputException("ProductDTOがnullです。");
        }
        if (dto.getCategory() == null) {
            throw new InvalidInputException("CategoryDTOがnullです。");
        }
        if (dto.getStock() == null) {
            throw new InvalidInputException("StockDTOがnullです。");
        }
        // 骨格を作り、カテゴリ・在庫を attach して合成する
        final Product skeleton = productMapper.toDomain(dto);
        skeleton.attachCategory(categoryMapper.toDomain(dto.getCategory()));
        skeleton.attachStock(stockMapper.toDomain(dto.getStock()));
        return skeleton;
    }

    /**
     * ドメイン集約 {@link Product} をネストされたDTO構造に変換する。
     *
     * @param domain Productエンティティ
     * @return ProductDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    public ProductDTO assembleDto(Product domain) {
        if (domain == null) {
            throw new InvalidInputException("Productがnullです。");
        }
        ProductDTO dto = productMapper.fromDomain(domain);
        if (domain.getCategory() != null) {
            dto.setCategory(categoryMapper.fromDomain(domain.getCategory()));
        }
        if (domain.getStock() != null) {
            dto.setStock(stockMapper.fromDomain(domain.getStock()));
        }
        return dto;
    }

    /**
     * Category エンティティを単独でDTO変換するユーティリティ（カテゴリ一覧などで利用）。
     *
     * @param category Categoryエンティティ
     * @return CategoryDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    public CategoryDTO toCategoryDto(Category category) {
        if (category == null) {
            throw new InvalidInputException("Categoryがnullです。");
        }
        return categoryMapper.fromDomain(category);
    }
}