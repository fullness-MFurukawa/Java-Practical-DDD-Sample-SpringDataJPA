package jp.co.fullness.ddd.application.product.usecase;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.NotFoundException;

/**
 * ユースケース:[商品を名前で検索する]を実現するインターフェイス。
 */
public interface SearchProductByNameUsecase {

    /**
     * 商品名を指定して商品情報を取得する。
     *
     * @param name 商品名
     * @return 該当する商品のDTO
     * @throws NotFoundException 指定された商品名の商品が存在しないとき
     */
    ProductDTO search(String name);
}