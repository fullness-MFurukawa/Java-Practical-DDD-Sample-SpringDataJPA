package jp.co.fullness.ddd.domain.model.product;

import java.util.Optional;

/**
 * ドメインリポジトリ：{@code ProductRepository}
 *
 * <p>このリポジトリは、ドメインエンティティ {@link Product} の
 * 永続化と再構築を担うインターフェイスです。
 * <br>DDDにおけるリポジトリの目的は、
 * 「ドメイン層からインフラ層(DBやORMなど)の実装を隠蔽し、
 * 集約を取得・保存するための契約(ポート)」を提供することです。
 *
 * <p>【ユビキタス言語】
 * <ul>
 *   <li>「商品(Product)」：販売対象となる製品や品目</li>
 *   <li>「商品名(ProductName)」：顧客が識別できる人間可読な名称</li>
 *   <li>「商品ID(ProductId)」：商品をシステム内で一意に識別する値オブジェクト</li>
 * </ul>
 *
 * <p>【設計上の意図】
 * <ul>
 *   <li>ドメイン層は永続化の仕組みに依存しない(jOOQ, MyBatis, JPAなどは非依存)</li>
 *   <li>アプリケーション層はこのインターフェイスを介してProductを操作する</li>
 *   <li>インフラ層でこの契約を実装し、実際のDBアクセスを担う</li>
 * </ul>
 *
 * <p>【責務】
 * <ul>
 *   <li>商品エンティティの新規登録(永続化)</li>
 *   <li>商品エンティティの変更(更新)</li>
 *   <li>商品名の一意性確認(存在チェック)</li>
 *   <li>商品IDまたは商品名による検索</li>
 * </ul>
 *
 * <p>例：
 * <pre>{@code
 * Product product = Product.createNew(...);
 * if (productRepository.existsByName(ProductName.of("万年筆"))) {
 *     throw new ExistsException("同名の商品が既に存在します");
 * }
 * productRepository.create(product);
 *
 * Product found = productRepository.findById(product.getProductId())
 *                 .orElseThrow(() -> new NotFoundException("商品が見つかりません"));
 * }</pre>
 */
public interface ProductRepository {

    /**
     * 新しい商品を永続化する。
     *
     * <p>ドメイン上の新規登録操作(例：「商品を登録する」ユースケース)で呼び出されます。
     * <br>同一名の商品が既に存在する場合は、ユースケース層で検証・制御を行うことを想定します。
     *
     * @param product 永続化対象の {@link Product} エンティティ
     */
    void create(Product product);

    /**
     * 既存の商品を変更(更新)する。
     *
     * <p>ドメイン上の変更操作(例：「商品を変更する」ユースケース)で呼び出されます。
     * <br>商品ID({@link ProductId})で対象を特定し、商品名・単価・在庫数などの状態を
     * 永続化層に反映します。集約ルートである {@link Product} を通じて、
     * 保持する在庫({@code Stock})も含めて一括で更新することを想定します。
     *
     * <p>商品名の一意性(同名商品の重複不可)は、更新対象の商品自身を除外して
     * 判定する必要があるため、ユースケース層で {@link #findByName(ProductName)} を用いて
     * 「同名商品が存在し、かつその商品IDが更新対象と異なる場合のみ不可」と検証することを想定します。
     *
     * @param product 変更内容を反映済みの {@link Product} エンティティ(商品IDで対象を特定する)
     */
    void update(Product product);

    /**
     * 指定された商品名が既に存在するかを確認する。
     *
     * <p>商品名の一意制約をドメインルールとして実現するために利用されます。
     * <br>例：「同じ商品名の登録は許可しない」。
     *
     * @param productName 確認対象の商品名(値オブジェクト)
     * @return 存在する場合は {@code true}、存在しない場合は {@code false}
     */
    boolean existsByName(ProductName productName);

    /**
     * 商品IDを指定して商品を取得する。
     *
     * <p>主キー検索に対応し、結果が存在しない場合は {@code Optional.empty()} を返します。
     * <br>この操作は通常、ユースケース「商品詳細の表示」や「登録確認」、
     * 「商品を変更する」ユースケースにおける変更前の取得などで使用されます。
     *
     * @param productId 商品ID(値オブジェクト)
     * @return 存在する場合は {@link Product} を保持する {@code Optional}、存在しない場合は {@code Optional.empty()}
     */
    Optional<Product> findById(ProductId productId);

    /**
     * 商品名を指定して商品を取得する。
     *
     * <p>主に「商品名で検索する」ユースケースや「登録済み商品の重複確認」などで利用されます。
     * <br>「商品を変更する」ユースケースでは、同名商品の重複判定(自分自身を除く)にも利用します。
     *
     * @param productName 商品名(値オブジェクト)
     * @return 存在する場合は {@link Product} を保持する {@code Optional}、存在しない場合は {@code Optional.empty()}
     */
    Optional<Product> findByName(ProductName productName);
}
