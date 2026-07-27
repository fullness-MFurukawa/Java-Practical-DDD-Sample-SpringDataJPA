package jp.co.fullness.ddd.application.product.usecase;

import java.util.List;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.exception.NotFoundException;

/**
 * <b>ユースケース: 商品を登録する</b> を実現するアプリケーション層のインターフェイス。
 *
 * <h3>役割</h3>
 * <ul>
 *   <li>プレゼンテーション層からの要求に応じて、商品登録に必要なアプリケーション処理を統括する。</li>
 *   <li>カテゴリの参照、存在確認、登録実行、登録結果の返却までを一貫した操作として提供する。</li>
 * </ul>
 *
 * <h3>非責務</h3>
 * <ul>
 *   <li>ドメインルールの実装（Entity/VOに委譲）</li>
 *   <li>永続化の詳細（Repository/インフラ層に委譲）</li>
 * </ul>
 */
public interface RegisterProductUsecase {

    /**
     * すべての商品カテゴリを取得する。
     *
     * @return 商品カテゴリリスト
     */
    List<CategoryDTO> getCategories();

    /**
     * 指定されたカテゴリIDでカテゴリを取得する。
     *
     * @param categoryId 商品カテゴリID（UUID文字列）
     * @return 検索結果のカテゴリDTO
     * @throws InvalidInputException ID形式が不正な場合など入力が不正なとき
     * @throws NotFoundException 指定IDのカテゴリが存在しないとき
     */
    CategoryDTO getCategoryById(String categoryId);

    /**
     * 指定された商品名が既に存在するかを検査する。
     * <p>存在していた場合は例外で通知する（呼び出し側はメッセージ表示等を行う）。</p>
     *
     * @param productName 商品名（非空）
     * @throws InvalidInputException 商品名が未指定/不正なとき
     * @throws ExistsException 同名の商品が既に存在するとき
     */
    void existsProduct(String productName);

    /**
     * 商品を登録する。
     * <p>登録後は、登録結果（ID等を含む最新状態）のDTOを返す。</p>
     *
     * @param product 登録対象の商品DTO（カテゴリ/在庫等を含む）
     * @return 登録後の商品DTO（ID等が反映された状態）
     * @throws InvalidInputException DTOの必須項目不足や変換不能など入力が不正なとき
     * @throws NotFoundException 指定されたカテゴリが存在しないとき
     * @throws ExistsException 同名の商品が既に存在するとき
     */
    ProductDTO addProduct(ProductDTO product);
}
