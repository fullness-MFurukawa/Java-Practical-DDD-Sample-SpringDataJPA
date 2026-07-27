package jp.co.fullness.ddd.application.product.usecase.interactor;

import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ddd.application.annotation.UseCase;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.application.product.usecase.SearchProductByNameUsecase;
import jp.co.fullness.ddd.domain.model.product.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * ユースケース:[商品を名前で検索する]を実現するインターフェイスの実装。
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchProductByNameInteractor implements SearchProductByNameUsecase {

    /** 商品サービス */
    private final ProductService service;

    /** DomainEntity と DTO の相互変換・組み立て */
    private final ProductDTOAssembler assembler;

    /**
     * 商品名を指定して商品情報を取得する。
     *
     * @param name 商品名
     * @return 該当する商品のDTO
     */
    @Override
    public ProductDTO search(String name) {
        // 名前で商品を検索
        var result = service.getProductByName(ProductName.of(name));
        // Product 集約を ProductDTO 集約に変換して返す
        return assembler.assembleDto(result);
    }
}