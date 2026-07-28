package jp.co.fullness.ddd.application.product.usecase;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.exception.NotFoundException;

/**
 * <b>ユースケース: 商品を変更する</b> を実現するアプリケーション層のインターフェイス。
 *
 * <h3>役割</h3>
 * <ul>
 *   <li>プレゼンテーション層からの要求に応じて、商品変更に必要なアプリケーション処理を統括する。</li>
 *   <li>変更対象の取得（編集用）、変更の適用、同名重複チェック、変更実行、変更結果の返却までを
 *       一貫した操作として提供する。</li>
 * </ul>
 *
 * <h3>非責務</h3>
 * <ul>
 *   <li>ドメインルールの実装（Entity/VOに委譲）</li>
 *   <li>永続化の詳細（Repository/インフラ層に委譲）</li>
 * </ul>
 *
 * <p>変更対象は商品の名称・単価・在庫数であり、カテゴリは変更対象外とする。</p>
 */
public interface UpdateProductUsecase {

    /**
     * 変更対象の商品を取得する（編集画面の初期表示などで利用）。
     *
     * @param productId 商品ID（UUID文字列）
     * @return 該当商品のDTO（カテゴリ・在庫を含む）
     * @throws InvalidInputException ID形式が不正な場合など入力が不正なとき
     * @throws NotFoundException 指定IDの商品が存在しないとき
     */
    ProductDTO getProduct(String productId);

    /**
     * 商品を変更する。
     * <p>変更後は、変更結果（最新状態）のDTOを返す。</p>
     *
     * @param product 変更内容を含む商品DTO（id・name・price・在庫数を使用。カテゴリは変更対象外）
     * @return 変更後の商品DTO
     * @throws InvalidInputException DTOの必須項目不足や変換不能など入力が不正なとき
     * @throws NotFoundException 指定IDの商品が存在しないとき
     * @throws ExistsException 指定された商品名が、変更対象以外の商品で既に使用されているとき
     */
    ProductDTO updateProduct(ProductDTO product);
}