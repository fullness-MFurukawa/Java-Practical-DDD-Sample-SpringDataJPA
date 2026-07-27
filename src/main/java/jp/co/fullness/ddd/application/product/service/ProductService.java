package jp.co.fullness.ddd.application.product.service;

import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;

/**
 * 商品に関するアプリケーションサービスインターフェイス。
 *
 * <p>ユースケースから呼び出されるドメイン操作の窓口を定義する。
 * Serviceは単一のEntity（ここではProduct）に対して作成し、複数のUseCaseから共通利用される。</p>
 */
public interface ProductService {

    /**
     * 指定された商品名が未登録であることを確認する（登録済みなら例外）。
     *
     * @param productName 商品名(VO)
     * @throws ExistsException 指定された商品名の商品が既に存在する場合
     */
    void existsProduct(ProductName productName);

    /**
     * 商品Idで商品を取得する。
     *
     * @param productId 商品Id(VO)
     * @return 商品
     * @throws NotFoundException 指定された商品Idに該当する商品が存在しない場合
     */
    Product getProductById(ProductId productId);

    /**
     * 商品名で商品を取得する。
     *
     * @param productName 商品名(VO)
     * @return 商品
     * @throws NotFoundException 指定された商品名に該当する商品が存在しない場合
     */
    Product getProductByName(ProductName productName);

    /**
     * 商品を登録する。
     *
     * @param product 登録対象商品(Entity)
     */
    void addProduct(Product product);
}