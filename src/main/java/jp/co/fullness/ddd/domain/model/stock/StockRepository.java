package jp.co.fullness.ddd.domain.model.stock;

/**
 * ドメインリポジトリ：{@code StockRepository}
 *
 * <p>このインターフェイスは、ドメイン層における「在庫(Stock)」エンティティの
 * 永続化および取得の抽象契約を定義します。
 * 実際のデータアクセスは、インフラストラクチャ層の実装クラス
 * (例：{@code StockRepositoryImpl})が担当します。</p>
 *
 * <p>DDDにおけるリポジトリは「永続化の詳細を隠し、ドメインモデルに自然な操作を提供する」
 * ことを目的としています。したがって、このインターフェイスでは
 * RDBやORM、SQLの存在を意識しないシンプルなメソッドシグネチャを定義します。</p>
 *
 * <p><b>責務:</b></p>
 * <ul>
 *   <li>ドメインエンティティ {@link Stock} の永続化(保存・更新)</li>
 *   <li>在庫情報の取得・再構築(必要に応じて拡張)</li>
 * </ul>
 *
 * <p><b>実装例:</b>
 * {@code StockRepositoryImpl}(インフラ層)で jOOQ / MyBatis / Spring Data JPA を利用して実現します。</p>
 *
 * @see Stock
 */
public interface StockRepository {

    /**
     * 新しい商品在庫を永続化する。
     *
     * <p>主に「商品登録」ユースケースなどで、
     * 商品と同時に初期在庫を登録する際に利用されます。
     * ドメイン層の {@link Stock} エンティティを
     * 永続化層のテーブル(例：{@code product_stock})へ書き込みます。</p>
     *
     * <p>引数の {@link Stock} は自己検証済みで不変条件を満たしている前提です。
     * 永続化に失敗した場合、実装側は非チェック例外(インフラ層の {@code InternalException} など)を
     * 送出することがあります。</p>
     *
     * @param stock 永続化対象の {@link Stock} エンティティ(不変条件が満たされている必要があります)
     */
    void create(Stock stock);
}
