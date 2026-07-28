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
     * <p>主に「商品を登録する」ユースケースで用いる。</p>
     *
     * @param productName 商品名(VO)
     * @throws ExistsException 指定された商品名の商品が既に存在する場合
     */
    void existsProduct(ProductName productName);

    /**
     * 指定された商品名が、更新対象の商品自身を除いて未使用であることを確認する（他商品が使用中なら例外）。
     *
     * <p>「商品を変更する」ユースケースの重複チェックに用いる。商品名を変更しない更新を
     * 許可するため、同名商品が存在しても、それが更新対象自身（{@code productId} と一致）であれば
     * 例外を投げない。すなわち「同名商品が存在し、かつその商品IDが更新対象と異なる場合のみ」例外とする。</p>
     *
     * @param productName 商品名(VO)
     * @param productId   更新対象の商品Id(VO) … この商品自身は重複判定から除外する
     * @throws ExistsException 指定された商品名の商品が、更新対象以外に既に存在する場合
     */
    void existsProductExcept(ProductName productName, ProductId productId);

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

    /**
     * 商品を変更(更新)する。
     *
     * <p>商品Idで対象を特定し、変更後の状態（名称・単価・在庫数）を永続化する。
     * 同名重複の検証は {@link #existsProductExcept(ProductName, ProductId)} で
     * 事前に行うことを想定する。</p>
     *
     * @param product 変更内容を反映済みの商品(Entity) … 商品Idで対象を特定する
     */
    void updateProduct(Product product);
}